package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class ApproveRequest extends CommonRequest {
    private String to;
    private BigInteger tokenId;

    public ApproveRequest(String nftContractAddress, String to, BigInteger tokenId) {
        super(nftContractAddress);
        this.to = to;
        this.tokenId = tokenId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigInteger getTokenId() {
        return tokenId;
    }

    public void setTokenId(BigInteger tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.checkAddress(to) && RequestUtils.nonNegative(tokenId);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
