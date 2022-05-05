package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class TokenIdCommonRequest extends CommonRequest {
    private BigInteger tokenId;

    public TokenIdCommonRequest(String nftContractAddress, BigInteger tokenId) {
        super(nftContractAddress);
        this.tokenId = tokenId;
    }

    public BigInteger getTokenId() {
        return tokenId;
    }

    public void setTokenId(BigInteger tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.nonNegative(tokenId);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
