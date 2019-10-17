package com.xcbeyond.xcloud.common.exception.handler;

import com.xcbeyond.xcloud.common.core.constant.GlobalConstant;
import com.xcbeyond.xcloud.common.core.data.ResponseResult;
import com.xcbeyond.xcloud.common.exception.BusinessException;
import com.xcbeyond.xcloud.common.exception.SystemException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

/**
 * 全局异常处理
 * @Auther: xcbeyond
 * @Date: 2019/5/28 15:19
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 业务逻辑异常。
     *  HTTP响应状态为200
     * @param businessException
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity businessExceptionHandler(BusinessException businessException) {
        ResponseResult result = businessException.getResult();
        return new ResponseEntity(result, GlobalConstant.HTTP_STATUS_200);
    }

    /**
     * 系统异常。
     *  HTTP响应状态为400
     * @param systemException
     * @return
     */
    @ExceptionHandler(value = SystemException.class)
    public ResponseEntity systemExceptionHandler(SystemException systemException) {
        ResponseResult result = systemException.getResult();
        return new ResponseEntity(result, GlobalConstant.HTTP_STATUS_400);
    }

    /**
     * SQL执行异常
     * @param sqlException
     * @return
     */
    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity sqlExceptionHandler(SQLException sqlException) {
        sqlException.printStackTrace();
        int statusCode = sqlException.getErrorCode();
        String msg = "SQL执行错误：" + sqlException.getMessage();
        ResponseResult result = new ResponseResult(statusCode, msg);
        return new ResponseEntity(result, GlobalConstant.HTTP_STATUS_400);
    }
}