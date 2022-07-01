package com.ancun.chain_storage.config;

import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyPairLoader {
  private Logger logger = LoggerFactory.getLogger(KeyPairLoader.class);

  @Value("${passwd}")
  private String passwd;

  @Autowired private Client client;

  public CryptoKeyPair loadKeyPair(String address) {
    String filePath = client.getCryptoSuite().getCryptoKeyPair().getP12KeyStoreFilePath(address);
    try {
      client.getCryptoSuite().loadAccount("p12", filePath, passwd);
    } catch (Exception e) {
      logger.error("load keyPair[{}] failed: {}", address, e);
      return null;
    }
    return client.getCryptoSuite().getCryptoKeyPair();
  }
}
