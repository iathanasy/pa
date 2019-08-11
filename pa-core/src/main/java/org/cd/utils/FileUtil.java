package org.cd.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 11:24
 **/
@Slf4j
public class FileUtil {

    /**
     * 根据 url 和 contentType 生成文件名, 去除非文件名字符
     *
     * @param url
     * @param contentType
     * @return String
     */
    public static String getFileNameByUrl(String url, String contentType) {
        url = url.replaceAll("[\\?/:*|<>\"]", "_");
        if (contentType!=null && contentType.lastIndexOf("/")>-1) {
            url += "." + contentType.substring(contentType.lastIndexOf("/") + 1);	// text/html、application/pdf
        }
        return url;
    }

    /**
     * 保存文本文件
     *
     * @param fileData
     * @param filePath
     * @param fileName
     */
    public static void saveFile(String fileData, String filePath, String fileName) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath, fileName)));
            out.writeChars(fileData);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 下载文件
     *
     * @param fileUrl
     * @param timeoutMillis
     * @param filePath
     * @param fileName
     */
    public static boolean downFile(String fileUrl, int timeoutMillis, String filePath, String fileName) {

        try {
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(timeoutMillis);

            InputStream inputStream = connection.getInputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(filePath, fileName)));

            byte[] buf = new byte[1024];
            int size;
            while (-1 != (size = inputStream.read(buf))) {
                bufferedOutputStream.write(buf, 0, size);
            }
            bufferedOutputStream.close();
            inputStream.close();

            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
