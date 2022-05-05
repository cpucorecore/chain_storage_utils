package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class TokenByIndexRequest extends CommonRequest {
    private BigInteger index;

    public TokenByIndexRequest(String nftContractAddress, BigInteger index) {
        super(nftContractAddress);
        this.index = index;
    }

    public BigInteger getIndex() {
        return index;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.nonNegative(index);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
