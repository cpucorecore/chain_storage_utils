package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;
import java.math.BigInteger;

public class NodeRegisterRequest implements Request {
  private BigInteger space;
  private String ext;

  public BigInteger getSpace() {
    return space;
  }

  public void setSpace(BigInteger space) {
    this.space = space;
  }

  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public NodeRegisterRequest() {}

  public NodeRegisterRequest(BigInteger space, String ext) {
    this.space = space;
    this.ext = ext;
  }

  @Override
  public boolean check() { // TODO check
    return space.longValue() > 0;
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
