package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class TokenOfOwnerByIndexRequest extends CommonRequest {
    private String owner;
    private BigInteger index;

    public TokenOfOwnerByIndexRequest(String nftContractAddress, String owner, BigInteger index) {
        super(nftContractAddress);
        this.owner = owner;
        this.index = index;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public BigInteger getIndex() {
        return index;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.checkAddress(owner) && RequestUtils.nonNegative(index);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
