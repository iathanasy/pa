package org.cd.pa.exception;

/**
 * @description:
 * @author: Mr.Wang
 * @create: 2019-08-10 23:13
 **/
public class PaException extends RuntimeException{

    public PaException(){super();}

    public PaException(String message){
        super(message);
    }
}
