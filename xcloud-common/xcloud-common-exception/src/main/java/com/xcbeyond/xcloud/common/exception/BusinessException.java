package com.xcbeyond.xcloud.common.exception;

import com.xcbeyond.xcloud.common.core.data.ResponseResult;

/**
 * 业务处理异常
 * @Auther: xcbeyond
 * @Date: 2019/5/28 11:20
 */
public class BusinessException extends BaseException {
    private ResponseResult result = new ResponseResult();

    public BusinessException(ResponseResult result) {
        super(result.getStatusCode()+ ":" + result.getMsg());
        this.result = result;
    }

    public BusinessException(int code, String msg) {
        super(code + ":" + msg);
        this.result.setStatusCode(code);
        this.result.setMsg(msg);
    }

    public BusinessException(ResponseResult result, Throwable cause) {
        super(result.getStatusCode() + ":" + result.getMsg(), cause);
        this.result = result;
    }

    public BusinessException(int code, String msg, Throwable cause) {
        super(code + ":" + msg, cause);
        this.result.setStatusCode(code);
        this.result.setMsg(msg);
    }

    public ResponseResult getResult() {
        return result;
    }

    public void setResult(ResponseResult result) {
        this.result = result;
    }
}
