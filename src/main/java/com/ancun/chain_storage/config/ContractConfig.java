package com.ancun.chain_storage.config;

import static com.ancun.chain_storage.config.Constants.FileStorageBytes32;
import static com.ancun.chain_storage.config.Constants.NodeManagerBytes32;
import static com.ancun.chain_storage.config.Constants.NodeStorageBytes32;
import static com.ancun.chain_storage.config.Constants.UserStorageBytes32;

import com.ancun.chain_storage.contracts.FileStorage;
import com.ancun.chain_storage.contracts.NodeManager;
import com.ancun.chain_storage.contracts.NodeStorage;
import com.ancun.chain_storage.contracts.Resolver;
import com.ancun.chain_storage.contracts.UserStorage;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContractConfig {
  Logger logger = LoggerFactory.getLogger(ContractConfig.class);

  @Autowired private ResolverConfig resolverConfig;

  @Autowired private Resolver resolver;

  @Autowired private Client client;

  @Bean
  public NodeManager nodeManager() {
    try {
      String address = resolver.getAddress(NodeManagerBytes32);
      return NodeManager.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    } catch (ContractException e) {
      logger.error("{}", e);
      return null;
    }
  }

  @Bean
  public NodeStorage nodeStorage() {
    try {
      String address = resolver.getAddress(NodeStorageBytes32);
      return NodeStorage.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    } catch (ContractException e) {
      logger.error("{}", e);
      return null;
    }
  }

  @Bean
  public UserStorage userStorage() {
    try {
      String address = resolver.getAddress(UserStorageBytes32);
      return UserStorage.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    } catch (ContractException e) {
      logger.error("{}", e);
      return null;
    }
  }

  @Bean
  public FileStorage fileStorage() {
    try {
      String address = resolver.getAddress(FileStorageBytes32);
      return FileStorage.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    } catch (ContractException e) {
      logger.error("{}", e);
      return null;
    }
  }
}
