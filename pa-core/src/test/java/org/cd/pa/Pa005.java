package org.cd.pa;

import lombok.Data;
import lombok.ToString;
import org.cd.pa.annotation.PageFieldSelect;
import org.cd.pa.annotation.PageSelect;
import org.cd.pa.conf.PaConf;
import org.cd.pa.parser.PageParser;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 11:04
 **/
public class Pa005 {

    @Data
    @ToString
    @PageSelect(cssQuery = ".post_list .post_item")
    public static class PageVo {

        @PageFieldSelect(cssQuery = ".titlelnk")
        private String name;
        @PageFieldSelect(cssQuery = ".post_item_summary")
        private String desc;
        @PageFieldSelect(cssQuery = ".titlelnk a" ,selectType = PaConf.SelectType.ATTR, selectVal = "abs:href")
        private String url;
    }

    public static void main(String[] args) {
        Pa pa = new Pa.Builder()
                .setUrls("https://www.cnblogs.com/sitehome/p/1")
                .setWhiteUrlRegexs("https://www.cnblogs.com/sitehome/p/\\d+")
                .setThreadCount(3)
                .setParser(new PageParser<PageVo>() {
                    @Override
                    public void parse(Page page, PageVo pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = page.getUrl();
                        System.out.println("------------------------------");
                        System.out.println(pageUrl + "：" + pageVo);
                    }
                })
                .build();
            pa.start();
    }
}
