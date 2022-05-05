package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

import java.math.BigInteger;

public class SaveReceiptRequest extends CommonRequest {
    private BigInteger tokenId;
    private String txId;
    private String ext;

    public SaveReceiptRequest(String contractAddress, BigInteger tokenId, String txId, String ext) {
        super(contractAddress);
        this.tokenId = tokenId;
        this.txId = txId;
        this.ext = ext;
    }

    public BigInteger getTokenId() {
        return tokenId;
    }

    public void setTokenId(BigInteger tokenId) {
        this.tokenId = tokenId;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Override
    public boolean check() {
        return super.check() && RequestUtils.checkTxId(txId) && null != ext;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
