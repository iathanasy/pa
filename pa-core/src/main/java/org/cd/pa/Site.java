package org.cd.pa;

import org.cd.pa.conf.PaConf;
import org.cd.pa.downloader.Downloader;
import org.cd.pa.downloader.HttpClientDownloader;
import org.cd.pa.parser.PageParser;
import org.cd.pa.proxy.ProxyMaker;
import org.cd.utils.RegexUtil;
import org.cd.utils.UrlUtil;
import lombok.Data;

import java.util.*;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-10 16:45
 **/
@Data
public class Site {


    private volatile boolean allowSpread = true;                                    // 允许扩散爬取，将会以现有URL为起点扩散爬取整站
    private Set<String> whiteUrlRegexs = Collections.synchronizedSet(new HashSet<String>());    // URL白名单正则，非空时进行URL白名单过滤页面
    private Downloader downloader = new HttpClientDownloader();                     // 页面下载器
    private PageParser parser;                                                      // 页面解析器

    private volatile Map<String, String> paramMap;                                  // 请求参数
    private volatile Map<String, String> cookieMap;                                 // 请求Cookie
    private volatile Map<String, String> headerMap;                                 // 请求Header
    private volatile ProxyMaker proxyMaker;                                         // 代理生成器
    private volatile String method;                                                          // 请求方式 Get Post
    private volatile String userAgent = PaConf.userAgentArray[new Random().nextInt(PaConf.userAgentArray.length)];       // 请求userAgent
    private volatile String referrer;                                                        // 请求Referrer
    private volatile String charset;                                                         // 编码
    private volatile int sleepTime = 5000;                                                   // 等待时间
    private volatile int retryTimes = 0;                                                     // 重试时间
    private volatile int cycleRetryTimes = 0;                                                // 循环重试时间
    private volatile int retrySleepTime = 1000;                                              // 重试等待时间
    private volatile int timeOut = 5000;                                                     // 超时时间
    private volatile int failRetryCount = 0;                                        // 失败重试次数，大于零时生效
    private volatile int pauseMillis = 0;                                           // 停顿时间，爬虫线程处理完页面之后进行主动停顿，避免过于频繁被拦截；
    private volatile boolean useGzip = true;                                                 // 使用gzip
    private volatile boolean disableCookieManagement = false;                                // 是否禁用Cookie


    public static Site me() {
        return new Site();
    }
    // util
    /**
     * valid url, include white url
     *
     * @param link
     * @return boolean
     */
    public boolean validWhiteUrl(String link){
        if (!UrlUtil.isUrl(link)) {
            return false;   // false if url invalid
        }

        if (whiteUrlRegexs!=null && whiteUrlRegexs.size()>0) {
            boolean underWhiteUrl = false;
            for (String whiteRegex: whiteUrlRegexs) {
                if (RegexUtil.matches(whiteRegex, link)) {
                    underWhiteUrl = true;
                }
            }
            if (!underWhiteUrl) {
                return false;   // check white
            }
        }
        return true;    // true if regex is empty
    }
}
