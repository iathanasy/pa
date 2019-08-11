package org.cd.pa;

import org.cd.pa.downloader.Downloader;
import org.cd.pa.parser.PageParser;
import org.cd.pa.proxy.ProxyMaker;
import org.cd.pa.queue.Queue;
import org.cd.pa.queue.stategy.LocalQueue;
import org.cd.pa.thread.PaThread;
import org.cd.pa.thread.SimpleThreadPoolExecutor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 爬虫入口
 * @author: Mr.Wang
 * @create: 2019-08-08 21:57
 **/
@Slf4j
@Getter
@Setter
public class Pa {

    //本地url队列
    private volatile Queue queue = new LocalQueue();
    // 运行时配置
    private volatile Site site = Site.me();

    //线程数
    private int threadCount = 1;
    //爬虫线程池
    private ThreadPoolExecutor crawlers =new SimpleThreadPoolExecutor("paThreadPool");
    //爬虫线程
    private List<PaThread>  crawlerThreads =new CopyOnWriteArrayList<>();


    // ---------------------- builder ----------------------
    public static class Builder{
        Pa pa = new Pa();

        /**
         * 设置队列
         * @param queue
         * @return
         */
        public Builder setQueue(Queue queue){
            pa.queue = queue;
            return this;
        }

        /**
         * 待爬的URL列表
         *
         * @param urls
         * @return Builder
         */
        public Builder setUrls(String... urls) {
            if (urls!=null && urls.length>0) {
                for (String url: urls) {
                    pa.queue.add(url);
                }
            }
            return this;
        }


        /**
         * 允许扩散爬取，将会以现有URL为起点扩散爬取整站
         *
         * @param allowSpread
         * @return Builder
         */
        public Builder setAllowSpread(boolean allowSpread) {
            pa.site.setAllowSpread(allowSpread);
            return this;
        }

        /**
         * URL白名单正则，非空时进行URL白名单过滤页面
         *
         * @param whiteUrlRegexs
         * @return Builder
         */
        public Builder setWhiteUrlRegexs(String... whiteUrlRegexs) {
            if (whiteUrlRegexs!=null && whiteUrlRegexs.length>0) {
                for (String whiteUrlRegex: whiteUrlRegexs) {
                    pa.site.getWhiteUrlRegexs().add(whiteUrlRegex);
                }
            }
            return this;
        }

        /**
         * 页面解析器
         *
         * @param parser
         * @return Builder
         */
        public Builder setParser(PageParser parser){
            pa.site.setParser(parser);
            return this;
        }

        /**
         * 页面下载器
         *
         * @param downloader
         * @return Builder
         */
        public Builder setDownloader(Downloader downloader){
            pa.site.setDownloader(downloader);
            return this;
        }

        // site
        /**
         * 请求参数
         *
         * @param paramMap
         * @return Builder
         */
        public Builder setParamMap(Map<String, String> paramMap){
            pa.site.setParamMap(paramMap);
            return this;
        }

        /**
         * 请求Cookie
         *
         * @param cookieMap
         * @return Builder
         */
        public Builder setCookieMap(Map<String, String> cookieMap){
            pa.site.setCookieMap(cookieMap);
            return this;
        }

        /**
         * 请求Header
         *
         * @param headerMap
         * @return Builder
         */
        public Builder setHeaderMap(Map<String, String> headerMap){
            pa.site.setHeaderMap(headerMap);
            return this;
        }

        /**
         * 请求UserAgent
         *
         * @param userAgent
         * @return Builder
         */
        public Builder setUserAgent(String userAgent){
            pa.site.setUserAgent(userAgent);
            return this;
        }

        /**
         * 请求Referrer
         *
         * @param referrer
         * @return Builder
         */
        public Builder setReferrer(String referrer){
            pa.site.setReferrer(referrer);
            return this;
        }

        /**
         * 请求方式 Post Get
         * @param method
         * @return
         */
        public Builder setMethod(String method){
            pa.site.setMethod(method);
            return this;
        }

        /**
         * 代理生成器
         *
         * @param proxyMaker
         * @return Builder
         */
        public Builder setProxyMaker(ProxyMaker proxyMaker){
            pa.site.setProxyMaker(proxyMaker);
            return this;
        }

        /**
         * 页面编码
         * @param charset
         * @return
         */
        public Builder setCharset(String charset){
            pa.site.setCharset(charset);
            return this;
        }

        /**
         * 等待时间
         * @param sleepTime
         * @return
         */
        public Builder setSleepTime(int sleepTime){
            pa.site.setSleepTime(sleepTime);
            return this;
        }

        /**
         * 重试时间
         * @param retryTimes
         * @return
         */
        public Builder setRetryTimes(int retryTimes){
            pa.site.setRetryTimes(retryTimes);
            return this;
        }

        /**
         * 循环重试时间
         * @param cycleRetryTimes
         * @return
         */
        public Builder setCycleRetryTimes(int cycleRetryTimes){
            pa.site.setCycleRetryTimes(cycleRetryTimes);
            return this;
        }

        /**
         * 重试等待时间
         * @param retrySleepTime
         * @return
         */
        public Builder setRetrySleepTime(int retrySleepTime){
            pa.site.setRetrySleepTime(retrySleepTime);
            return this;
        }

        /**
         * 超时时间
         * @param timeOut
         * @return
         */
        public Builder setTimeOut(int timeOut){
            pa.site.setTimeOut(timeOut);
            return this;
        }

        /**
         * 失败重试次数，大于零时生效
         *
         * @param failRetryCount
         * @return Builder
         */
        public Builder setFailRetryCount(int failRetryCount){
            if (failRetryCount > 0) {
                pa.site.setFailRetryCount(failRetryCount);
            }
            return this;
        }

        // thread
        /**
         * 爬虫并发线程数
         *
         * @param threadCount
         * @return Builder
         */
        public Builder setThreadCount(int threadCount) {
            pa.threadCount = threadCount;
            pa.getSite().getDownloader().setThread(threadCount);
            return this;
        }

        public Pa build() {
            return pa;
        }

    }


    // ---------------------- crawler thread ----------------------

    /**
     * 启动
     */
    public void start(){
        if (queue == null) {
            throw new RuntimeException("pa queue can not be null.");
        }
        if (queue.size() <= 0) {
            throw new RuntimeException("pa indexUrl can not be empty.");
        }
        if (site == null) {
            throw new RuntimeException("pa site can not be empty.");
        }
        if (threadCount<1 || threadCount>1000) {
            throw new RuntimeException("pa threadCount invalid, threadCount : " + threadCount);
        }
        if (site.getDownloader() == null) {
            throw new RuntimeException("pa downloader can not be null.");
        }
        if (site.getParser() == null) {
            throw new RuntimeException("pa parser can not be null.");
        }

        log.info(">>>>>>>>>>> pa start ...");

        for (int i = 0; i < threadCount; i++) {
            PaThread paThread = new PaThread(this);
            crawlerThreads.add(paThread);
        }
        for (PaThread crawlerThread: crawlerThreads) {
            crawlers.execute(crawlerThread);
        }
        //new Thread(new ThreadPoolMonitor(crawlers, "PaThreadPool")).start();
        crawlers.shutdown();
    }

    /**
     * 尝试终止
     */
    public void tryFinish(){
        boolean isRunning = false;
        for (PaThread crawlerThread: crawlerThreads) {
            if (crawlerThread.isRunning()) {
                isRunning = true;
                break;
            }
        }
        boolean isEnd = queue.size()==0 && !isRunning;
        if (isEnd) {
            log.info(">>>>>>>>>>> pa is finished.");
            stop();
        }
    }

    /**
     * 终止
     */
    public void stop(){
        for (PaThread crawlerThread: crawlerThreads) {
            crawlerThread.toStop();
        }
        crawlers.shutdownNow();
        log.info(">>>>>>>>>>> pa stop.");
    }

}
