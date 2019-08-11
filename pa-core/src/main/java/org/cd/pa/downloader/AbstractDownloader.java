package org.cd.pa.downloader;

import org.cd.pa.Request;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-10 15:01
 **/
public abstract class AbstractDownloader implements Downloader{


    protected void onSuccess(Request request) {
    }
    protected void onError(Request request) {
    }
}
