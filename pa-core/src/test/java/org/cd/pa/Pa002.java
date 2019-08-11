package org.cd.pa;

import org.cd.pa.annotation.PageFieldSelect;
import org.cd.pa.annotation.PageSelect;
import org.cd.pa.conf.PaConf;
import org.cd.pa.parser.PageParser;
import org.cd.utils.FileUtil;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 11:22
 **/
public class Pa002 {
    @Data
    @ToString
    @PageSelect(cssQuery = "body")
    public static class PageVo {

        @PageFieldSelect(cssQuery = ".post_content img", selectType = PaConf.SelectType.ATTR, selectVal = "abs:src")
        private List<String> images;
    }

    public static void main(String[] args) {
        Pa pa = new Pa.Builder()
                .setUrls("https://www.krnet.cc/html/article/2768.html")
                .setWhiteUrlRegexs("https://www.krnet\\.cc/html/article/\\d+.html")
                .setThreadCount(5)
                .setParser(new PageParser<PageVo>() {
                    @Override
                    public void parse(Page page, PageVo pageVo) {
                        String filePath = "E:\\images";
                        if (pageVo.getImages()!=null && pageVo.getImages().size() > 0) {
                            Set<String> imagesSet = new HashSet<>(pageVo.getImages());
                            for (String img: imagesSet) {

                                // 下载图片文件
                                String fileName = FileUtil.getFileNameByUrl(img, null);
                                boolean ret = FileUtil.downFile(img, 5000 , filePath, fileName);
                                System.out.println("down images " + (ret?"success":"fail") + "：" + img);
                            }
                        }
                    }
                })
                .build();
        pa.start();
    }
}
