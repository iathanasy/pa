package org.cd.pa;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.cd.pa.annotation.PageFieldSelect;
import org.cd.pa.annotation.PageSelect;
import org.cd.pa.conf.PaConf;
import org.cd.pa.parser.PageParser;
import org.cd.utils.RegexUtil;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 18:17
 **/
public class Cnblog {

    @Data
    @ToString
    @PageSelect(cssQuery = "#main .post_item")
    public static class PageVo {

        private String hash;
        @PageFieldSelect(cssQuery = ".titlelnk")
        private String name;
        @PageFieldSelect(cssQuery = ".post_item_summary")
        private String desc;
        @PageFieldSelect(cssQuery = ".titlelnk" ,selectType = PaConf.SelectType.ATTR, selectVal = "abs:href")
        private String url;
        @PageFieldSelect(cssQuery = ".post_item_foot")
        private String createTime;
    }

    public static void main(String[] args) {
        Pa pa = new Pa.Builder()
                .setUrls("https://www.cnblogs.com/sitehome/p/1")
                .setAllowSpread(false)
                .setWhiteUrlRegexs("https://www.cnblogs.com/sitehome/p/\\d+")
                .setThreadCount(3)
                .setParser(new PageParser<PageVo>() {

                    @Override
                    public void parse(Page page, PageVo pageVo) {
                        System.out.println(page.getUrl()+": "+ pageVo);
                    }
                })
                .build();
        pa.start();
    }
}
