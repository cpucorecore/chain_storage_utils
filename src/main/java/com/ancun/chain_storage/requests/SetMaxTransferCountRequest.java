package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class SetMaxTransferCountRequest extends CommonRequest {
    private BigInteger maxTransferCount;

    public SetMaxTransferCountRequest(String nftContractAddress, BigInteger maxTransferCount) {
        super(nftContractAddress);
        this.maxTransferCount = maxTransferCount;
    }

    public BigInteger getMaxTransferCount() {
        return maxTransferCount;
    }

    public void setMaxTransferCount(BigInteger maxTransferCount) {
        this.maxTransferCount = maxTransferCount;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.nonNegative(maxTransferCount);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
