package com.xson.common.bean;

/**
 * @author xson
 * 带状态和描述的bean
{
"status":0,
"msg":"成功"
}
 */
public class BaseBean {

    /**
     * msg : success
     * code : 200
     */

    private String msg;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
