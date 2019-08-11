package org.cd.pa.downloader;

import org.cd.pa.Page;
import org.cd.pa.Request;
import org.cd.pa.Site;
import org.cd.pa.proxy.Proxy;
import org.cd.pa.proxy.ProxyMaker;
import org.cd.utils.CharsetUtils;
import org.cd.utils.HttpClientUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-10 15:13
 **/
@Slf4j
@Setter
public class HttpClientDownloader extends AbstractDownloader {

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
    private HttpClientUtil httpClientUtils = new HttpClientUtil();
    private ProxyMaker proxyMaker;

    private CloseableHttpClient getHttpClient(Request request, Site site) {
        if (request == null || site == null) {
            throw new NullPointerException("request or site can not be null");
        }
        String domain = request.getUrl();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientUtils.getClient(request, site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    @Override
    public Page download(Request request, Site site) {
        if (request == null || site == null) {
            throw new NullPointerException("request or site can not be null");
        }
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(request, site);
        Proxy proxy = proxyMaker != null ? proxyMaker.make() : null;
        HttpClientUtil.HttpClientRequestContext requestContext = httpClientUtils.convert(request, site);

        Page page = Page.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : site.getCharset(), httpResponse);
            onSuccess(request);
            log.info("downloading page success {}", request.getUrl());
            return page;
        } catch (IOException e) {
            log.warn("download page {} error", request.getUrl(), e);
            onError(request);
            return page;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
            if (proxyMaker != null && proxy != null) {
                proxyMaker.addProxy(proxy);
            }
        }
    }

    @Override
    public void setThread(int threadNum) {
        httpClientUtils.setPoolSize(threadNum);
    }


    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse) throws IOException {
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        Page page = new Page();
        page.setContentData(bytes);
        if (charset == null) {
            charset = getHtmlCharset(contentType, bytes);
        }
        page.setCharset(charset);
        page.setContentType(contentType);
        page.setContent(new String(bytes, charset));

        page.setUrl(request.getUrl());
        page.setRequest(request);
        page.setCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);

        return page;
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = Charset.defaultCharset().name();
            log.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
        }
        return charset;
    }
}
