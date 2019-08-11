package org.cd.pa.parser;

import org.cd.pa.Page;
import org.cd.pa.Request;

/**
 * @description: page parser
 * @author: Mr.Wang
 * @create: 2019-08-10 18:55
 **/
public abstract class PageParser<T> implements Parser{

    /**
     * 页面加载前
     * @param request
     */
    public void preParse(Request request) {
        // TODO
    }

    /**
     *
     * @param page 页面数据
     * @param pageVo 解析实体
     */
    public abstract void parse(Page page, T pageVo);
}
