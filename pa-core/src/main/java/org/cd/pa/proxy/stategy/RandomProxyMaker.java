package org.cd.pa.proxy.stategy;

import org.cd.pa.proxy.Proxy;
import org.cd.pa.proxy.ProxyMaker;

import java.util.Random;

/**
 * @description: 随机获取代理
 * @author: Mr.Wang
 * @create: 2019-08-10 15:21
 **/
public class RandomProxyMaker extends ProxyMaker {

    private Random random = new Random();

    @Override
    public Proxy make() {
        if(this.proxyList == null || this.proxyList.size() == 0){
            return null;
        }

        if (this.proxyList.size() == 1) {
            this.proxyList.get(0);
        }
        //返回随机
        return super.proxyList.get(random.nextInt(super.proxyList.size()));
    }
}
