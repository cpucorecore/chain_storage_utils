package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

public class DeployCSContractRequest implements Request {
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private String name;

  public DeployCSContractRequest() {}

  public DeployCSContractRequest(String name) {
    this.name = name;
  }

  @Override
  public boolean check() {
    return true;
  }

  @Override
  public String toJsonString() {
    return JSON.toJSONString(this);
  }

  @Override
  public String getContractAddress() {
    return null;
  }
}
