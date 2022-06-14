package com.ancun.chain_storage.service_account.impl;

import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;

public class ChainAccount {
  public ChainAccount(KeyStoreEntity keyStore, CryptoKeyPair cryptoKeyPair) {
    this.keyStore = keyStore;
    this.cryptoKeyPair = cryptoKeyPair;
  }

  public KeyStoreEntity getKeyStore() {
    return keyStore;
  }

  public void setKeyStore(KeyStoreEntity keyStore) {
    this.keyStore = keyStore;
  }

  public CryptoKeyPair getCryptoKeyPair() {
    return cryptoKeyPair;
  }

  public void setCryptoKeyPair(CryptoKeyPair cryptoKeyPair) {
    this.cryptoKeyPair = cryptoKeyPair;
  }

  private KeyStoreEntity keyStore;
  private CryptoKeyPair cryptoKeyPair;
}
