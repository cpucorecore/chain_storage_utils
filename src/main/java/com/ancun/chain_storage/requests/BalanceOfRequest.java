package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

public class BalanceOfRequest extends CommonRequest {
    private String owner;

    public BalanceOfRequest(String nftContractAddress, String owner) {
        super(nftContractAddress);
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.checkAddress(owner);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
