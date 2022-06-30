package com.ancun.chain_storage.constants;

public enum Response {
  SUCCESS(0, "成功"),
  CREATE_ACCOUNT_FAIL(100, "create account fail"),
  INVALID_ACCOUNT(101, "invalid account"),
  GET_CHAIN_ACCOUNT_FAILED(101, "get chain account failed"),
  INVALID_REQUEST(200, "invalid request"),
  DEPLOY_CONTRACT_FAILED(300, "deploy contract failed"),
  LOAD_CONTRACT_FAILED(301, "load contract failed"),
  CALL_CONTRACT_FAILED(302, "contract call failed"),
  UNKNOWN_SETTING_KEY(400, "unknown setting key"),
  ;

  private int code;
  private String msg;

  Response(int code, String msg) {
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
