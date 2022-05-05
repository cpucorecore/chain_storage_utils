package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class DeployNFTContractRequest implements Request {
    private String name;
    private String symbol;
    private Boolean issueTransferAllowed;
    private BigInteger transferInterval;
    private BigInteger maxTransferCount;

    public DeployNFTContractRequest() {}

    public DeployNFTContractRequest(
            String name,
            String symbol,
            boolean issueTransferAllowed,
            BigInteger transferInterval,
            BigInteger maxTransferCount) {
        this.name = name;
        this.symbol = symbol;
        this.issueTransferAllowed = issueTransferAllowed;
        this.transferInterval = transferInterval;
        this.maxTransferCount = maxTransferCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Boolean isIssueTransferAllowed() {
        return issueTransferAllowed;
    }

    public void setIssueTransferAllowed(Boolean issueTransferAllowed) {
        this.issueTransferAllowed = issueTransferAllowed;
    }

    public BigInteger getTransferInterval() {
        return transferInterval;
    }

    public void setTransferInterval(BigInteger transferInterval) {
        this.transferInterval = transferInterval;
    }

    public BigInteger getMaxTransferCount() {
        return maxTransferCount;
    }

    public void setMaxTransferCount(BigInteger maxTransferCount) {
        this.maxTransferCount = maxTransferCount;
    }

    @Override
    public boolean check() {
        return (null != name
                        && name.length() > 0
                        && name.length() <= RequestUtils.MAX_NFT_NAME_LENGTH)
                && (null != symbol
                        && symbol.length() > 0
                        && symbol.length() <= RequestUtils.MAX_NFT_SYMBOL_LENGTH)
                && RequestUtils.nonNegative(transferInterval)
                && RequestUtils.nonNegative(maxTransferCount);
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    @Override
    public String getContractAddress() {
        return null;
    }
}
