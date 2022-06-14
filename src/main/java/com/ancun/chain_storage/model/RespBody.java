package com.ancun.chain_storage.model;

import com.ancun.chain_storage.constants.ResponseInfo;

public class RespBody<T> {
  private int code = ResponseInfo.SUCCESS.getCode();
  private String msg = ResponseInfo.SUCCESS.getMsg();

  private T data;

  public RespBody(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public RespBody(ResponseInfo info, T data) {
    this.code = info.getCode();
    this.msg = info.getMsg();
    this.data = data;
  }

  public RespBody(ResponseInfo info) {
    this.code = info.getCode();
    this.msg = info.getMsg();
  }

  public void setResponseInfo(ResponseInfo info) {
    this.code = info.getCode();
    this.msg = info.getMsg();
  }

  public RespBody(int code, String msg, T data) {
    this(code, msg);
    this.data = data;
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

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "RespBody{" + "code=" + code + ", msg=" + msg + '\'' + ", data=" + data + '}';
  }
}
