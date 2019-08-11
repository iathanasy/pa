package org.cd.pa.downloader;

import org.cd.pa.Page;
import org.cd.pa.Request;
import org.cd.pa.Site;

/**
 * @description: 下载器
 * @author: Mr.Wang
 * @create: 2019-08-10 14:56
 **/
public interface Downloader {

    Page download(Request request, Site site);

    void setThread(int threadNum);
}
