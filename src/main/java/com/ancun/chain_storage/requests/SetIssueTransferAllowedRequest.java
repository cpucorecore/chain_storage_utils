package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

public class SetIssueTransferAllowedRequest extends CommonRequest {
    private Boolean issueTransferAllowed;

    public SetIssueTransferAllowedRequest(Boolean issueTransferAllowed) {
        this.issueTransferAllowed = issueTransferAllowed;
    }

    public SetIssueTransferAllowedRequest(String nftContractAddress, Boolean issueTransferAllowed) {
        super(nftContractAddress);
        this.issueTransferAllowed = issueTransferAllowed;
    }

    public Boolean getIssueTransferAllowed() {
        return issueTransferAllowed;
    }

    public void setIssueTransferAllowed(Boolean issueTransferAllowed) {
        this.issueTransferAllowed = issueTransferAllowed;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
