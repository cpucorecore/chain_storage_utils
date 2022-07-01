package com.ancun.chain_storage.constants;

public enum Response {
  SUCCESS(0, "成功"),
  LOAD_CHAIN_ACCOUNT_FAILED(100, "load chain account failed"),
  INVALID_REQUEST(200, "invalid request"),
  CALL_CONTRACT_FAILED(300, "contract call failed"),
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
