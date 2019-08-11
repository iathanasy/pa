package org.cd.pa;

import org.cd.pa.proxy.Proxy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-08 22:15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    private String url;
    // 请求参数
    private Map<String, String> paramMap;
    // 请求Cookie
    private Map<String, String> cookieMap;
    // 请求Header
    private Map<String, String> headerMap;
    //请求方式 Get Post
    private String method;
    //
    private String userAgent;
    // 请求Referrer
    private String referrer;
    // 超时时间，毫秒
    private int timeoutMillis;
    // 编码
    private String charset;
    private Proxy proxy;

    public Request(String url){
        this.url = url;
    }
}
