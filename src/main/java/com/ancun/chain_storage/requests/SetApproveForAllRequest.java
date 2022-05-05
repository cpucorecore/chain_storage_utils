package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

public class SetApproveForAllRequest extends CommonRequest {
    private String operator;
    private Boolean approved;

    public SetApproveForAllRequest(String nftContractAddress, String operator, boolean approved) {
        super(nftContractAddress);
        this.operator = operator;
        this.approved = approved;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.checkAddress(operator);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
