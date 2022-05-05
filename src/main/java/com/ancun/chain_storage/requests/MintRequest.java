package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class MintRequest extends CommonRequest {
    private BigInteger tokenId;
    private String tokenURI;
    private String to;

    public MintRequest(String nftContractAddress, BigInteger tokenId, String tokenURI, String to) {
        super(nftContractAddress);
        this.tokenId = tokenId;
        this.tokenURI = tokenURI;
        this.to = to;
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

    public String getTokenURI() {
        return tokenURI;
    }

    public void setTokenURI(String tokenURI) {
        this.tokenURI = tokenURI;
    }

    @Override
    public boolean check() {
        return super.check()
                && RequestUtils.checkAddress(to)
                && null != tokenURI
                && tokenURI.length() > 0
                && tokenURI.length() <= RequestUtils.MAX_TOKEN_URI_LENGTH
                && RequestUtils.nonNegative(tokenId);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
