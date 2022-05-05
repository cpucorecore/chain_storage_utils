package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class TransferFromRequest extends CommonRequest {
    private String from;
    private String to;
    private BigInteger tokenId;
    private String data;

    public TransferFromRequest(
            String nftContractAddress, String from, String to, BigInteger tokenId, String data) {
        super(nftContractAddress);
        this.from = from;
        this.to = to;
        this.tokenId = tokenId;
        this.data = data;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean check() {
        return super.check()
                && RequestUtils.checkAddress(from)
                && RequestUtils.checkAddress(to)
                && RequestUtils.nonNegative(tokenId);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
