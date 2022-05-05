package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class SetTransferIntervalRequest extends CommonRequest {
    private BigInteger transferInterval;

    public SetTransferIntervalRequest(BigInteger transferInterval) {
        this.transferInterval = transferInterval;
    }

    public SetTransferIntervalRequest(String nftContractAddress, BigInteger transferInterval) {
        super(nftContractAddress);
        this.transferInterval = transferInterval;
    }

    public BigInteger getTransferInterval() {
        return transferInterval;
    }

    public void setTransferInterval(BigInteger transferInterval) {
        this.transferInterval = transferInterval;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.nonNegative(transferInterval);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
