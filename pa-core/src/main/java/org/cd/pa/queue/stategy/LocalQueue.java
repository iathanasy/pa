package org.cd.pa.queue.stategy;

import org.cd.pa.exception.PaException;
import org.cd.pa.queue.AbstractQueue;
import org.cd.utils.UrlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 08:33
 **/
@Slf4j
public class LocalQueue extends AbstractQueue {

    // url
    private volatile LinkedBlockingQueue<String> unVisitedUrlQueue = new LinkedBlockingQueue<String>();     // 待采集URL池
    private volatile Set<String> visitedUrlSet = Collections.synchronizedSet(new HashSet<String>());        // 已采集URL池

    @Override
    public boolean add(String link) {
        if (!UrlUtil.isUrl(link)) {
            log.debug(">>>>>>>>>>> pa add fail, link not valid: {}", link);
            return false; // check URL格式
        }
        if (visitedUrlSet.contains(link)) {
            log.debug(">>>>>>>>>>> pa add fail, link repeate: {}", link);
            return false; // check 未访问过
        }
        if (unVisitedUrlQueue.contains(link)) {
            log.debug(">>>>>>>>>>> pa add fail, link visited: {}", link);
            return false; // check 未记录过
        }
        unVisitedUrlQueue.add(link);
        log.info(">>>>>>>>>>> pa add success, link: {}", link);
        return true;
    }

    @Override
    public String get() {
        String link = null;
        try {
            //获取待采集url
            link = unVisitedUrlQueue.take();
        } catch (InterruptedException e) {
            throw new PaException("LocalQueue.ge interrupted.");
        }
        //添加已采集
        if (link != null) {
            visitedUrlSet.add(link);
        }
        return link;
    }

    @Override
    public int size() {
        //带采集数量
        return unVisitedUrlQueue.size();
    }
}
