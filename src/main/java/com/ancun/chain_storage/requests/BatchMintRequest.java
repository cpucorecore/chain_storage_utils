package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;
import java.util.List;

public class BatchMintRequest extends CommonRequest {
    private String to;
    private BigInteger startTokenId;
    private BigInteger amount;
    private List<String> tokenURIs;

    public BatchMintRequest() {}

    public BatchMintRequest(
            String nftContractAddress,
            String to,
            BigInteger startTokenId,
            BigInteger amount,
            List<String> tokenURIs) {
        super(nftContractAddress);
        this.to = to;
        this.startTokenId = startTokenId;
        this.amount = amount;
        this.tokenURIs = tokenURIs;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigInteger getStartTokenId() {
        return startTokenId;
    }

    public void setStartTokenId(BigInteger startTokenId) {
        this.startTokenId = startTokenId;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public List<String> getTokenURIs() {
        return tokenURIs;
    }

    public void setTokenURIs(List<String> tokenURIs) {
        this.tokenURIs = tokenURIs;
    }

    @Override
    public boolean check() {
        return super.check()
                && RequestUtils.checkAddress(to)
                && RequestUtils.nonNegative(startTokenId)
                && (1 == amount.compareTo(BigInteger.valueOf(0)))
                && (amount.intValue() == tokenURIs.size());
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
