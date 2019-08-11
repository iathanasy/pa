package org.cd.utils;

public class Config {

	 /**
     * 是否持久化到数据库
     */
    public static boolean dbEnable = false;
    
    /**
     * driverClassName
     */
    public static String driverClassName = "com.mysql.jdbc.Driver";
    /**
     * db.name
     */
    public static String dbName = "cnblog";
    /**
     * db.username
     */
    public static String dbUsername = "root";
    /**
     * db.host
     */
    public static String dbHost = "127.0.0.1";
    /**
     * db.password
     */
    public static String dbPassword = "root";
    /**
     * db.url
     */
    public static String dbUrl = "jdbc:mysql://" + dbHost + ":3306/" + dbName + "?characterEncoding=utf8";
    
    /**
     * 创建表语句
     */
    public static String createUrlTable;

    /**
     * 创建表语句
     */
    public static String createUserTable;
    
    
}
