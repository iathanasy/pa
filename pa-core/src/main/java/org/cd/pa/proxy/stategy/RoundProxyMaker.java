package org.cd.pa.proxy.stategy;

import org.cd.pa.proxy.Proxy;
import org.cd.pa.proxy.ProxyMaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-10 15:24
 **/
public class RoundProxyMaker extends ProxyMaker {

    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public Proxy make() {
        if (super.proxyList==null || super.proxyList.size()==0) {
            return null;
        }

        if (super.proxyList.size() == 1) {
            super.proxyList.get(0);
        }

        int countVal = count.incrementAndGet();
        if (countVal > 100000) {
            countVal = 0;
            count.set(countVal);
        }
        //
        return super.proxyList.get(countVal % super.proxyList.size());
    }
}
