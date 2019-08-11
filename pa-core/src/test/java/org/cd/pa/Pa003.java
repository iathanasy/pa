package org.cd.pa;

import org.cd.pa.parser.PageParser;
import org.cd.pa.proxy.Proxy;
import org.cd.pa.proxy.ProxyMaker;
import org.cd.pa.proxy.stategy.RoundProxyMaker;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 17:51
 **/
public class Pa003 {

    public static void main(String[] args) {

        // 设置代理池
        ProxyMaker proxyMaker = new RoundProxyMaker()
                .addProxy(new Proxy("111.230.99.192", 8118));

        Pa pa = new Pa.Builder()
                .setUrls("http://2019.ip138.com/ic.asp")
                .setAllowSpread(false)
                .setProxyMaker(proxyMaker)
                .setParser(new PageParser<Object>() {
                    @Override
                    public void parse(Page page, Object pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = page.getUrl();
                        System.out.println(pageUrl + "：" + page);
                    }
                })
                .build();
        pa.start();
    }
}
