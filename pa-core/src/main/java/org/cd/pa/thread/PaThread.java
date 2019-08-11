package org.cd.pa.thread;

import org.cd.pa.Pa;
import org.cd.pa.Page;
import org.cd.pa.Request;
import org.cd.pa.annotation.PageFieldSelect;
import org.cd.pa.annotation.PageSelect;
import org.cd.pa.conf.PaConf;
import org.cd.pa.exception.PaException;
import org.cd.pa.parser.JsonParser;
import org.cd.pa.proxy.Proxy;
import org.cd.utils.FieldReflectionUtil;
import org.cd.utils.JsoupUtil;
import org.cd.utils.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description: 爬虫线程
 * @author: Mr.Wang
 * @create: 2019-08-11 09:24
 **/
@Slf4j
public class PaThread implements Runnable {

    private Pa pa;
    private boolean running;
    private boolean toStop;

    public PaThread(Pa pa){
        this.pa = pa;
        this.running = true;
        this.toStop = false;
    }

    public void toStop() {
        this.toStop = true;
    }
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while(!toStop){
            try {
                // ------- url ----------
                running = false;
                pa.tryFinish();//尝试终止
                String link = pa.getQueue().get();
                running = true;
                log.info(">>>>>>>>>>> pa, process link : {}", link);
                if (!UrlUtil.isUrl(link)) {
                    continue;
                }

                // failover
                for (int i = 0; i < (1 + pa.getSite().getFailRetryCount()); i++) {

                    boolean ret = false;
                    try {
                        // make request
                        Request request = makePageRequest(link);

                        // pre parse
                        pa.getSite().getParser().preParse(request);

                        // parse
                        if ( pa.getSite().getParser() instanceof JsonParser) {
                            ret = processJsonPage(request);
                        } else {
                            ret = processPage(request);
                        }
                    } catch (Throwable e) {
                        log.info(">>>>>>>>>>> pa proocess error.", e);
                    }

                    if (pa.getSite().getPauseMillis() > 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(pa.getSite().getPauseMillis());
                        } catch (InterruptedException e) {
                            log.info(">>>>>>>>>>> pa thread is interrupted. 2{}", e.getMessage());
                        }
                    }
                    if (ret) {
                        break;
                    }
                }

            } catch (Throwable e) {
                if (e instanceof InterruptedException) {
                    log.info(">>>>>>>>>>> pa thread is interrupted. {}", e.getMessage());
                } else if (e instanceof PaException) {
                    log.info(">>>>>>>>>>> pa thread {}", e.getMessage());
                } else {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }


    /**
     * make page request
     *
     * @param link
     * @return PageRequest
     */
    private Request makePageRequest(String link){
        String userAgent = pa.getSite().getUserAgent();
        Proxy proxy = null;
        if (pa.getSite().getProxyMaker() != null) {
            proxy =pa.getSite().getProxyMaker().make();
        }

        Request request = new Request();
        request.setUrl(link);
        request.setParamMap(pa.getSite().getParamMap());
        request.setCookieMap(pa.getSite().getCookieMap());
        request.setHeaderMap(pa.getSite().getHeaderMap());
        request.setUserAgent(userAgent);
        request.setReferrer(pa.getSite().getReferrer());
        request.setMethod(pa.getSite().getMethod());
        request.setTimeoutMillis(pa.getSite().getTimeOut());
        request.setProxy(proxy);

        return request;
    }


    /**
     * process non page
     * @param request
     * @return boolean
     */
    private boolean processJsonPage(Request request){
        JsonParser jsonParser = (JsonParser)pa.getSite().getParser();

        String pagesource = pa.getSite().getDownloader().download(request, pa.getSite()).getContent();
        if (pagesource == null) {
            return false;
        }
        jsonParser.parse(request.getUrl(), pagesource);
        return true;
    }


    /**
     * process page
     * @param request
     * @return boolean
     */
    private boolean processPage(Request request) throws IllegalAccessException, InstantiationException {
        Page page = pa.getSite().getDownloader().download(request, pa.getSite());
        Document html = Jsoup.parse(page.getContent());

        if (html == null) {
            return false;
        }

        // ------- child link list (FIFO队列,广度优先) ----------
        if (pa.getSite().isAllowSpread()) {     // limit child spread
            Set<String> links = JsoupUtil.findLinks(html, request);
            if (links != null && links.size() > 0) {
                for (String item : links) {
                    if ( pa.getSite().validWhiteUrl(item)) {      // limit unvalid-child spread
                        pa.getQueue().add(item);
                    }
                }
            }
        }

        // ------- pagevo ----------
        if (! pa.getSite().validWhiteUrl(request.getUrl())) {     // limit unvalid-page parse, only allow spread child, finish here
            return true;
        }

        // pagevo class-field info
        Class pageVoClassType = Object.class;

        Type pageVoParserClass =  pa.getSite().getParser().getClass().getGenericSuperclass();
        if (pageVoParserClass instanceof ParameterizedType) {
            Type[] pageVoClassTypes = ((ParameterizedType)pageVoParserClass).getActualTypeArguments();
            pageVoClassType = (Class) pageVoClassTypes[0];
        }

        PageSelect pageVoSelect = (PageSelect) pageVoClassType.getAnnotation(PageSelect.class);
        String pageVoCssQuery = (pageVoSelect!=null && pageVoSelect.cssQuery()!=null && pageVoSelect.cssQuery().trim().length()>0)?pageVoSelect.cssQuery():"html";

        // pagevo document 2 object
        Elements pageVoElements = html.select(pageVoCssQuery);

        if (pageVoElements != null && pageVoElements.hasText()) {
            for (Element pageVoElement : pageVoElements) {

                Object pageVo = pageVoClassType.newInstance();

                Field[] fields = pageVoClassType.getDeclaredFields();
                if (fields!=null) {
                    for (Field field: fields) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            continue;
                        }


                        // field origin value
                        PageFieldSelect fieldSelect = field.getAnnotation(PageFieldSelect.class);
                        String cssQuery = null;
                        PaConf.SelectType selectType = null;
                        String selectVal = null;
                        if (fieldSelect != null) {
                            cssQuery = fieldSelect.cssQuery();
                            selectType = fieldSelect.selectType();
                            selectVal = fieldSelect.selectVal();
                        }
                        if (cssQuery==null || cssQuery.trim().length()==0) {
                            continue;
                        }

                        // field value
                        Object fieldValue = null;

                        if (field.getGenericType() instanceof ParameterizedType) {
                            ParameterizedType fieldGenericType = (ParameterizedType) field.getGenericType();
                            if (fieldGenericType.getRawType().equals(List.class)) {

                                //Type gtATA = fieldGenericType.getActualTypeArguments()[0];
                                Elements fieldElementList = pageVoElement.select(cssQuery);
                                if (fieldElementList!=null && fieldElementList.size()>0) {

                                    List<Object> fieldValueTmp = new ArrayList<Object>();
                                    for (Element fieldElement: fieldElementList) {

                                        String fieldElementOrigin = JsoupUtil.parseElement(fieldElement, selectType, selectVal);
                                        if (fieldElementOrigin==null || fieldElementOrigin.length()==0) {
                                            continue;
                                        }
                                        try {
                                            fieldValueTmp.add(FieldReflectionUtil.parseValue(field, fieldElementOrigin));
                                        } catch (Exception e) {
                                            log.error(e.getMessage(), e);
                                        }
                                    }

                                    if (fieldValueTmp.size() > 0) {
                                        fieldValue = fieldValueTmp;
                                    }
                                }
                            }
                        } else {

                            Elements fieldElements = pageVoElement.select(cssQuery);
                            String fieldValueOrigin = null;
                            if (fieldElements!=null && fieldElements.size()>0) {
                                fieldValueOrigin = JsoupUtil.parseElement(fieldElements.get(0), selectType, selectVal);
                            }

                            if (fieldValueOrigin==null || fieldValueOrigin.length()==0) {
                                continue;
                            }

                            try {
                                fieldValue = FieldReflectionUtil.parseValue(field, fieldValueOrigin);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        }

                        if (fieldValue!=null) {
                            /*PropertyDescriptor pd = new PropertyDescriptor(field.getName(), pageVoClassType);
                            Method method = pd.getWriteMethod();
                            method.invoke(pageVo, fieldValue);*/

                            field.setAccessible(true);
                            field.set(pageVo, fieldValue);
                        }
                    }
                }

                // pagevo output
                 pa.getSite().getParser().parse(page, pageVo);
            }
        }

        return true;
    }

}
