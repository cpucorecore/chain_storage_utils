package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class MintExtRequest extends CommonRequest {
    private String to;
    private BigInteger tokenId;
    private String name;
    private String description;

    public MintExtRequest(
            String nftContractAddress,
            String to,
            BigInteger tokenId,
            String name,
            String description) {
        super(nftContractAddress);
        this.tokenId = tokenId;
        this.name = name;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean check() {
        return super.check()
                && RequestUtils.checkAddress(to)
                && null != name
                && name.length() > 0
                && name.length() < RequestUtils.MAX_NFT_METADATA_NAME_LENGTH
                && null != description
                && description.length() > 0
                && description.length() < RequestUtils.MAX_METADATA_DESCRIPTION_LENGTH
                && RequestUtils.nonNegative(tokenId);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
