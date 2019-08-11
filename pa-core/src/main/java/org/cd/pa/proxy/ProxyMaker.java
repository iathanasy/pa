package org.cd.pa.proxy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-10 15:18
 **/
public abstract class ProxyMaker {

    protected List<Proxy> proxyList = new CopyOnWriteArrayList<Proxy>();            // 请求代理池，对抗反采集策略规则WAF

    public ProxyMaker addProxy(Proxy proxy) {
        this.proxyList.add(proxy);
        return this;
    }

    public ProxyMaker addProxy(Proxy... proxys) {
        this.proxyList.addAll(Arrays.asList(proxys));
        return this;
    }

    public ProxyMaker addProxyList(List<Proxy> proxyList) {
        this.proxyList.addAll(proxyList);
        return this;
    }

    public ProxyMaker clear() {
        this.proxyList.clear();
        return this;
    }

    /**
     * make proxy
     *
     * @return Proxy
     */
    public abstract Proxy make();
}
