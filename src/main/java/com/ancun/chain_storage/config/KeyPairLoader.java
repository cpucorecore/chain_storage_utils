package com.ancun.chain_storage.config;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyPairLoader {
  @Value("${passwd}")
  private String passwd;

  @Autowired private Client client;

  public CryptoKeyPair loadKeyPair(String address) {
    String filePath = client.getCryptoSuite().getCryptoKeyPair().getP12KeyStoreFilePath(address);
    client.getCryptoSuite().loadAccount("p12", filePath, passwd);
    return client.getCryptoSuite().getCryptoKeyPair();
  }
}
