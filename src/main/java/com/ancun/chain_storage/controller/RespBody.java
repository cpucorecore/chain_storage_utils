package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.constants.Response;

public class RespBody<T> {
  private int code;
  private String msg;

  private T data;

  public RespBody(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public RespBody(Response info, T data) {
    this.code = info.getCode();
    this.msg = info.getMsg();
    this.data = data;
  }

  public RespBody(Response info) {
    this.code = info.getCode();
    this.msg = info.getMsg();
  }

  public void setResponseInfo(Response info) {
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
