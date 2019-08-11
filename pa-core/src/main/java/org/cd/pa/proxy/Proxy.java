package org.cd.pa.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-08 23:13
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Proxy{

    private String ip;
    private int port;
    private String username;
    private String password;
    private String type;
    private String location;
    private String anonymous;
    private long responseTime;// 代理测试响应时间

    public Proxy(String ip, int port){
        this.ip = ip;
        this.port = port;
    }
}
