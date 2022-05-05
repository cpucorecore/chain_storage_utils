package com.ancun.chain_storage.service_account;

import com.ancun.chain_storage.service_account.impl.ChainAccount;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;

import java.io.IOException;

public interface AccountService {
    ChainAccount createChainAccount(String password) throws IOException;

    ChainAccount getChainAccount(String address, String password) throws Exception;

    CryptoKeyPair getReadonlyKeyPair();
}
