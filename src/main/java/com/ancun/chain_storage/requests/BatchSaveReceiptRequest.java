package com.ancun.chain_storage.requests;

import java.math.BigInteger;

public class BatchSaveReceiptRequest extends CommonRequest {
    private BigInteger startTokenId;
    private BigInteger count;
    private String txId;
    private String ext;

    public BatchSaveReceiptRequest(
            String contractAddress,
            BigInteger startTokenId,
            BigInteger count,
            String txId,
            String ext) {
        super(contractAddress);
        this.startTokenId = startTokenId;
        this.count = count;
        this.txId = txId;
        this.ext = ext;
    }

    public BigInteger getStartTokenId() {
        return startTokenId;
    }

    public void setStartTokenId(BigInteger startTokenId) {
        this.startTokenId = startTokenId;
    }

    public BigInteger getCount() {
        return count;
    }

    public void setCount(BigInteger count) {
        this.count = count;
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
}
