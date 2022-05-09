package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class UserAddFileRequest implements Request {
    private String cid;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public BigInteger getSize() {
        return size;
    }

    public void setSize(BigInteger size) {
        this.size = size;
    }

    public BigInteger getDuration() {
        return duration;
    }

    public void setDuration(BigInteger duration) {
        this.duration = duration;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public UserAddFileRequest() {
    }

    private BigInteger size;
    private BigInteger duration;
    private String ext;
    @Override
    public boolean check() {// TODO check
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
