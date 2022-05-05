package com.ancun.chain_storage.constants;

public enum ChainStorageResponseInfo {
    SUCCESS(0, "成功"),
    INVALID_REQUEST(1000, "invalid request");

    private int code;
    private String msg;

    ChainStorageResponseInfo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
