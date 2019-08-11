package org.cd.pa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-08 23:31
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Page {

    private Request request;
    private String url;
    private String content;
    private byte[] contentData;
    private String contentType;
    private String encoding;
    private String charset;
    private int code;
    private boolean downloadSuccess = true;
    private Map<String, Object> fields = new LinkedHashMap<String, Object>();


    /**
     * 失败
     * @return
     */
    public static Page fail(){
        Page page = new Page();
        page.setDownloadSuccess(false);
        return page;
    }
}
