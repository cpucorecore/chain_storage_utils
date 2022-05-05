package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;

public class SetBaseURIRequest extends CommonRequest {
    private String baseURI;

    public SetBaseURIRequest(String nftContractAddress, String baseURI) {
        super(nftContractAddress);
        this.baseURI = baseURI;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public boolean check() {
        return super.check() && baseURI.length() <= RequestUtils.MAX_BASE_URI_LENGTH;
    }

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
