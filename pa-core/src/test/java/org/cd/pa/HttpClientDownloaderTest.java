package org.cd.pa;

import org.cd.pa.downloader.HttpClientDownloader;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-10 23:16
 **/
public class HttpClientDownloaderTest {

    public static void main(String[] args) {
        HttpClientDownloader downloader = new HttpClientDownloader();
        String url="https://www.cnblogs.com/iathanasy/p/10220451.html";
        Request request = new Request(url);
        Page page = downloader.download(request, Site.me());
        System.out.println(page);
    }
}
