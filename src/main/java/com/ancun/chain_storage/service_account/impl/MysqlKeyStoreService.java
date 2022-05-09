package com.ancun.chain_storage.service_account.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MysqlKeyStoreService implements KeyStoreService {
    @Autowired private KeyStoreRepository keyStoreRepository;

    @Override
    public void saveKeyStore(KeyStoreEntity keyStore) {
        keyStoreRepository.save(keyStore);
    }

    @Override
    public KeyStoreEntity loadKeyStore(String address) {
        return keyStoreRepository.findByAddress(address);
    }

    @Override
    public Boolean exists(String address) {
        return keyStoreRepository.existsByAddress(address);
    }
}
