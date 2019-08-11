package org.cd.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-10 15:56
 **/
public class RegexUtil {

    /**
     * 正则匹配
     * @param regex	: 正则表达式
     * @param str	: 待匹配字符串
     * @return boolean
     */
    public static boolean matches(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 正则匹配
     * @param regex	: 正则表达式
     * @param str	: 待匹配字符串
     * @return String
     */
    public static String group(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

    private static final String URL_REGEX = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * url格式校验
     *
     * @param str
     * @return boolean
     */
    public static boolean isUrl(String str) {
        if (str==null || str.trim().length()==0) {
            return false;
        }
        return matches(URL_REGEX, str);
    }

    public static void main(String[] args) {
       Boolean flag = matches("https://www.krnet\\.cc/html/article/\\d+.html", "https://www.krnet.cc/html/article/3547w.html");
        System.out.println(flag);
    }

}
