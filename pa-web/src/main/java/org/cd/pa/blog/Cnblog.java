package org.cd.pa.blog;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.cd.dao.ConnectionManager;
import org.cd.pa.Pa;
import org.cd.pa.Page;
import org.cd.pa.annotation.PageFieldSelect;
import org.cd.pa.annotation.PageSelect;
import org.cd.pa.conf.PaConf;
import org.cd.pa.parser.PageParser;
import org.cd.utils.MD5Util;
import org.cd.utils.RegexUtil;

import java.sql.Connection;

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
        Connection connection = ConnectionManager.getConnection();
        Pa pa = new Pa.Builder()
                //首页
                /*.setUrls("https://www.cnblogs.com/sitehome/p/1")
                .setWhiteUrlRegexs("https://www.cnblogs.com/sitehome/p/\\d+")*/
                //精华
                /*.setUrls("https://www.cnblogs.com/pick/1")
                .setWhiteUrlRegexs("https://www.cnblogs.com/pick/\\d+/")*/
                //全部
                .setUrls("https://www.cnblogs.com")
                .setWhiteUrlRegexs("https://www.cnblogs.com/cate/\\w+/",//分类
                        "https://www.cnblogs.com/cate/\\w+/\\d+",//分页
                        "https://www.cnblogs.com/candidate/page\\d+",//候选
                        "https://www.cnblogs.com/news/\\d+",//新闻
                        "https://www.cnblogs.com/pick/\\d+/")
                .setThreadCount(5)
                .setParser(new PageParser<PageVo>() {
                    @Override
                    public void parse(Page page, PageVo pageVo) {
                        String releaseTime = RegexUtil.group("\\d{4}-\\d{1,2}-\\d{1,2}", pageVo.getCreateTime());
                        if (!StringUtils.isBlank(releaseTime)) {
                            pageVo.setCreateTime(releaseTime);
                        }
                        String hash = MD5Util.getMD5(pageVo.getName() + pageVo.getUrl());
                        pageVo.setHash(hash);
                        // 解析封装 PageVo 对象
                        String pageUrl = page.getUrl();
                        System.out.println(pageUrl + "：" + pageVo);
                        CnblogDao dao = new CnblogDao();
                       Long result = dao.insert(connection, pageVo);
                        System.out.println("--------------------------> "+result);
                    }
                })
                .build();
        pa.start();
    }
}
