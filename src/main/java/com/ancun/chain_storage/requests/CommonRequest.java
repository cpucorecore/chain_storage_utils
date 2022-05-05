package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

public class CommonRequest implements Request {
    protected String contractAddress;

    public CommonRequest() {}

    public CommonRequest(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    @Override
    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    @Override
    public boolean check() {
        return RequestUtils.checkAddress(contractAddress);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
