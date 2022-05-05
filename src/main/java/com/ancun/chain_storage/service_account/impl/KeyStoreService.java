package com.ancun.chain_storage.service_account.impl;

public interface KeyStoreService {
    void saveKeyStore(KeyStoreEntity keyStore);

    KeyStoreEntity loadKeyStore(String address);
}
