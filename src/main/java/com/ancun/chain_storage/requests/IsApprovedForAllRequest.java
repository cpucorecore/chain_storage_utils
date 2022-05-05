package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

public class IsApprovedForAllRequest extends CommonRequest {
    private String owner;
    private String operator;

    public IsApprovedForAllRequest(String nftContractAddress, String owner, String operator) {
        super(nftContractAddress);
        this.owner = owner;
        this.operator = operator;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public boolean check() {
        return super.check()
                && RequestUtils.checkAddress(owner)
                && RequestUtils.checkAddress(operator);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
