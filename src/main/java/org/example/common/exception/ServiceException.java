package org.example.common.exception;

import lombok.Data;

@Data
public class ServiceException extends RuntimeException{
    private Integer code;
    private String msg;

    public ServiceException(String msg){
        super(msg);
        this.code = 500;
        this.msg = msg;
    }

}
