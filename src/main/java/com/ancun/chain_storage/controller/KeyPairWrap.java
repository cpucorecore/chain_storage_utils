package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.model.RespBody;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;

public class KeyPairWrap {
    public KeyPairWrap(CryptoKeyPair keyPair, RespBody<String> resp) {
        this.keyPair = keyPair;
        this.resp = resp;
    }

    public CryptoKeyPair keyPair;
    public RespBody<String> resp;
}
