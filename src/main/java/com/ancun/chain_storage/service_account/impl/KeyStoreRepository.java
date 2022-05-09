package com.ancun.chain_storage.service_account.impl;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyStoreRepository extends JpaRepository<KeyStoreEntity, Long> {
    KeyStoreEntity findByAddress(String address);
    Boolean existsByAddress(String address);
}
