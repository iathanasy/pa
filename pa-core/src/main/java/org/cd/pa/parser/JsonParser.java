package org.cd.pa.parser;

import org.cd.pa.Page;

/**
 * @description: Json页面解析
 * @author: Mr.Wang
 * @create: 2019-08-10 23:06
 **/
public abstract class JsonParser extends PageParser {


    @Override
    public void parse(Page page, Object pageVo) {
        //TODO  不分析页，输出页源
    }

    public abstract void parse(String url, String pageSource);
}
