package org.cd.pa.queue;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-11 08:27
 **/
public interface Queue{

    boolean add(String link);

    String get();

    int size();
}
