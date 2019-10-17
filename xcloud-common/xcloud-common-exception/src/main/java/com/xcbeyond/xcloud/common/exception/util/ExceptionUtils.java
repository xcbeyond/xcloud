package com.xcbeyond.xcloud.common.exception.util;

import com.xcbeyond.xcloud.common.core.data.ResponseResult;
import com.xcbeyond.xcloud.common.exception.BusinessException;
import com.xcbeyond.xcloud.common.exception.SystemException;
import com.xcbeyond.xcloud.common.exception.parser.ErrorCodeParser;

/**
 * 异常工具类
 * @Auther: xcbeyond
 * @Date: 2019/5/27 09:37
 */
public class ExceptionUtils {
    /**
     * 业务处理异常
     * @param errCode   异常码
     * @return
     */
    public static BusinessException businessException(String errCode) {
        return new BusinessException(createResult(errCode));
    }

    /**
     * 业务处理异常
     * @param errCode   异常码
     * @param args  错误描述信息中的参数
     * @return
     */
    public static BusinessException businessException(String errCode, String... args) {
        return new BusinessException(createResult(errCode, args));
    }

    /**
     * 系统级异常
     * @param errCode   异常码
     * @return
     */
    public static SystemException systemException(String errCode) {
        return new SystemException(createResult(errCode));
    }

    /**
     * 系统级异常
     * @param errCode   异常码
     * @param args  错误描述信息中的参数
     * @return
     */
    public static SystemException systemException(String errCode, String... args) {
        return new SystemException(createResult(errCode, args));
    }


    private static ResponseResult createResult(String errCode) {
        return new ResponseResult(Integer.valueOf(errCode,16), getErrorMsg(errCode));
    }

    private static ResponseResult createResult(String errCode, String msg) {
        return new ResponseResult(Integer.valueOf(errCode,16), msg);
    }

    private static ResponseResult createResult(String errCode, String[] args) {
        return new ResponseResult(Integer.valueOf(errCode,16), getErrorMsg(errCode, args));
    }

    /**
     * 获取错误信息
     * @param errCode   错误码
     * @return
     */
    private static String getErrorMsg(String errCode) {
        return ErrorCodeParser.getErrorDesc(errCode);
    }

    /**
     * 获取错误信息
     * @param errCode   错误码
     * @param args  错误描述信息中的参数
     * @return
     */
    private static String getErrorMsg(String errCode, String[] args) {
        return ErrorCodeParser.getParseErrorDesc(errCode, args);
    }
}
