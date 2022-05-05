package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.Evidence;
import com.ancun.chain_storage.model.RespBody;

public class ContextEvidence {
    public ContextEvidence(Evidence contract, RespBody<String> resp) {
        this.contract = contract;
        this.resp = resp;
    }

    public Evidence contract;
    public RespBody<String> resp;
}
