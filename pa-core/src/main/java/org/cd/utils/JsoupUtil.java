package org.cd.utils;

import org.cd.pa.Request;
import org.cd.pa.conf.PaConf;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 10:54
 **/
@Slf4j
public class JsoupUtil {

    /**
     * 抽取元素数据
     *
     * @param fieldElement
     * @param selectType
     * @param selectVal
     * @return String
     */
    public static String parseElement(Element fieldElement, PaConf.SelectType selectType, String selectVal) {
        String fieldElementOrigin = null;
        if (PaConf.SelectType.HTML == selectType) {
            fieldElementOrigin = fieldElement.html();
        } else if (PaConf.SelectType.VAL == selectType) {
            fieldElementOrigin = fieldElement.val();
        } else if (PaConf.SelectType.TEXT == selectType) {
            fieldElementOrigin = fieldElement.text();
        } else if (PaConf.SelectType.ATTR == selectType) {
            fieldElementOrigin = fieldElement.attr(selectVal);
        }  else if (PaConf.SelectType.HAS_CLASS == selectType) {
            fieldElementOrigin = String.valueOf(fieldElement.hasClass(selectVal));
        }  else {
            fieldElementOrigin = fieldElement.toString();
        }
        return fieldElementOrigin;
    }

    /**
     * 获取页面上所有超链接地址 （<a>标签的href值）
     *
     * @param html  页面文档
     * @return Set<String>
     */
    public static Set<String> findLinks(Document html, Request request) {

        if (html == null) {
            return null;
        }
        String baseUrl = UrlUtil.domain(request.getUrl());
        // element
        /**
         *
         * Elements resultSelect = html.select(tagName);	// 选择器方式
         * Element resultId = html.getElementById(tagName);	// 元素ID方式
         * Elements resultClass = html.getElementsByClass(tagName);	// ClassName方式
         * Elements resultTag = html.getElementsByTag(tagName);	// html标签方式 "body"
         *
         */
        Elements hrefElements = html.select("a[href]");
        // 抽取数据
        Set<String> links = new HashSet<String>();
        if (hrefElements!=null && hrefElements.size() > 0) {
            for (Element item : hrefElements) {
               // String href = item.attr("abs:href");    // href、abs:href
                String href = item.attr("href");    // href、abs:href
                if(href == null) continue;
                if(!href.toLowerCase().startsWith("http"))
                    href = baseUrl+ href;
                if (UrlUtil.isUrl(href)) {
                    links.add(href);
                }
            }
        }
        return links;
    }

    /**
     * 获取页面上所有图片地址 （<a>标签的href值）
     *
     * @param html
     * @return Set<String>
     */
    public static Set<String> findImages(Document html) {
        Elements imgs = html.getElementsByTag("img");

        Set<String> images = new HashSet<String>();
        if (imgs!=null && imgs.size() > 0) {
            for (Element element: imgs) {
                String imgSrc = element.attr("abs:src");
                images.add(imgSrc);
            }
        }

        return images;
    }

}
