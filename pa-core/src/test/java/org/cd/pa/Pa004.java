package org.cd.pa;

import lombok.Data;
import lombok.ToString;
import org.cd.pa.annotation.PageFieldSelect;
import org.cd.pa.annotation.PageSelect;
import org.cd.pa.parser.JsonParser;
import org.cd.pa.parser.PageParser;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 11:04
 **/
public class Pa004 {

    public static void main(String[] args) {
        Pa pa = new Pa.Builder()
                .setUrls("http://news.baidu.com/widget?id=LocalNews&ajax=json")
                .setParser(new JsonParser() {
                    @Override
                    public void parse(String url, String pageSource) {
                        System.out.println(url + ": " + pageSource);
                    }
                })
                .build();
            pa.start();
    }
}
