package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

public class KVRequest implements Request{
    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    private String k;
    private String v;

    public KVRequest(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public KVRequest() {
    }

    @Override
    public boolean check() {
        return k.length()>0 &k.length()<1024 && v.length()>0 && v.length()<1024;
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
