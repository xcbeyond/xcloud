package com.xcbeyond.xcloud.common.core.data;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.xcbeyond.xcloud.common.core.constant.GlobalConstant;
import com.xcbeyond.xcloud.common.core.utils.ObjectUtils;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * 请求响应结果
 * @Auther: xcbeyond
 * @Date: 2019/10/24 16:24
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseResult implements Serializable {
    //状态码
    private int statusCode;
    //提示信息
    private String msg;
    //结果数据
    private Object data;

    public ResponseResult() {

    }

    public ResponseResult(int statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public ResponseResult(int statusCode, String msg, Object data) {
        this.statusCode = statusCode;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseEntity success() {
        ResponseResult result = new ResponseResult(GlobalConstant.SUCCESS, GlobalConstant.SUCCESS_MSG);
        return ResponseEntity.ok(result);
    }

    public static ResponseEntity success(String msg) {
        ResponseResult result = new ResponseResult(GlobalConstant.SUCCESS, msg);
        return ResponseEntity.ok(result);
    }

    public static ResponseEntity success(Object data) {
        ResponseResult result = new ResponseResult(GlobalConstant.SUCCESS, GlobalConstant.SUCCESS_MSG, data);
        return ResponseEntity.ok(result);
    }

    public static ResponseEntity success(String msg, Object data) {
        ResponseResult result = new ResponseResult(GlobalConstant.SUCCESS, msg, data);
        return ResponseEntity.ok(result);
    }

    public static ResponseEntity failed(int statusCode, String msg, Object data) {
        ResponseResult result = new ResponseResult(GlobalConstant.SUCCESS, GlobalConstant.SUCCESS_MSG, data);
        return ResponseEntity.ok(result);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 重写toString方法，让ResponseResult对象以json字符串形式存在
     * @return
     *  Json字符串
     */
    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("statusCode", String.format("0x%08x", this.statusCode));
        json.put("msg", this.msg);
        if (null != this.data) {
            json.put("data", ObjectUtils.objectToMap(this.data));
        }
        return json.toJSONString();
    }
}