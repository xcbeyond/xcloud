package com.xcbeyond.xcloud.common.exception;

import com.xcbeyond.xcloud.common.core.data.ResponseResult;

/**
 * 系统级异常
 * @Auther: xcbeyond
 * @Date: 2019/5/28 16:26
 */
public class SystemException extends BaseException{
    private ResponseResult result = new ResponseResult();

    public SystemException(ResponseResult result) {
        super(result.getStatusCode()+ ":" + result.getMsg());
        this.result = result;
    }

    public SystemException(int code, String msg) {
        super(code + ":" + msg);
        this.result.setStatusCode(code);
        this.result.setMsg(msg);
    }

    public SystemException(ResponseResult result, Throwable cause) {
        super(result.getStatusCode() + ":" + result.getMsg(), cause);
        this.result = result;
    }

    public SystemException(int code, String msg, Throwable cause) {
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
