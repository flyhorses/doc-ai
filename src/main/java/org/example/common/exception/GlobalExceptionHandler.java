package org.example.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException  e){
        log.error("业务异常",e);
        return Result.error(e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e)
    {
        log.error("系统异常",e);
        return Result.error("系统内部错误，请稍后重试");
    }
}
