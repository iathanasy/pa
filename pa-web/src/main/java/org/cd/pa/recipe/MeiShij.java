package org.cd.pa.recipe;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.cd.pa.Pa;
import org.cd.pa.Page;
import org.cd.pa.annotation.PageFieldSelect;
import org.cd.pa.annotation.PageSelect;
import org.cd.pa.conf.PaConf;
import org.cd.pa.parser.PageParser;
import org.cd.utils.RegexUtil;
import java.util.List;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 16:13
 **/
public class MeiShij {

    @Data
    @ToString
    @PageSelect(cssQuery = ".main .clearfix")
    public static class PageVo {
        @PageFieldSelect(cssQuery = ".info1 .title")
        private String title;//名称
        @PageFieldSelect(cssQuery = ".cp_body_left .materials p")
        private String desc; //描述
        @PageFieldSelect(cssQuery = ".cp_header .cp_headerimg_w img", selectType = PaConf.SelectType.ATTR, selectVal = "abs:src")
        private List<String> images;//菜谱图片
        @PageFieldSelect(cssQuery = ".info3 .info strong")
        private String releaseTime;//发布时间
    }


    public static void main(String[] args) {
        Pa pa = new Pa.Builder()
                .setUrls("https://www.meishij.net/")
                .setWhiteUrlRegexs("[https|http]+://[\\w]+.meishij\\.net/(chufang|china-food|hongpei|pengren|shicai|zuofan)+/\\w+/",
                        "[https|http]+://[\\w]+.meishij\\.net/(chufang|china-food|hongpei|pengren|shicai|zuofan)+/\\w+/?&page=\\d",
                        "https://www.meishij\\.net/zuofa/\\w+\\.html"
                        )
                .setThreadCount(5)
                .setParser(new PageParser<PageVo>() {
                    @Override
                    public void parse(Page page, PageVo pageVo) {
                        if(!StringUtils.isBlank(pageVo.getReleaseTime())){
                            String releaseTime = RegexUtil.group("\\d{4}-\\d{1,2}-\\d{1,2}", pageVo.getReleaseTime());
                            if (!StringUtils.isBlank(releaseTime)) {
                                pageVo.setReleaseTime(releaseTime);
                            }
                        }
                        System.out.println(page.getUrl() + ": " + pageVo);
                    }
                })
                .build();
        pa.start();
    }
}


