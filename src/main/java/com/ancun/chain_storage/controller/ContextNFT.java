package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.NFT;
import com.ancun.chain_storage.model.RespBody;

public class ContextNFT {
    public ContextNFT(NFT contract, RespBody<String> resp) {
        this.contract = contract;
        this.resp = resp;
    }

    public NFT contract;
    public RespBody<String> resp;
}
