package org.cd.pa;

import org.cd.pa.annotation.PageFieldSelect;
import org.cd.pa.annotation.PageSelect;
import org.cd.pa.parser.PageParser;
import lombok.Data;
import lombok.ToString;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 11:04
 **/
public class Pa001 {

    @Data
    @ToString
    @PageSelect(cssQuery = "#search-projects-ulist .project")
    public static class PageVo {

        @PageFieldSelect(cssQuery = ".repository")
        private String repository;

        @PageFieldSelect(cssQuery = ".description")
        private String description;
    }

    public static void main(String[] args) {
        Pa pa = new Pa.Builder()
                .setUrls("https://gitee.com/chuanwazi/projects?page=1")
                .setWhiteUrlRegexs("https://gitee\\.com/chuanwazi/projects\\?page=\\d+")
                .setThreadCount(3)
                .setParser(new PageParser<PageVo>() {
                    @Override
                    public void parse(Page page, PageVo pageVo) {
                        // 解析封装 PageVo 对象
                        String pageUrl = page.getUrl();
                        System.out.println(pageUrl + "：" + pageVo.toString());
                    }
                })
                .build();
            pa.start();
    }
}
