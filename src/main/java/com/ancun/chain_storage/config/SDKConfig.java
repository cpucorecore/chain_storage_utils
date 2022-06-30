package com.ancun.chain_storage.config;

import static org.fisco.bcos.sdk.model.CryptoType.SM_TYPE;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SDKConfig {
  Logger logger = LoggerFactory.getLogger(SDKConfig.class);

  @Value("${GroupId}")
  private Integer groupId;

  @Autowired private FileConfig fileConfig;

  @Bean
  public BcosSDK bcosSDK() {
    ConfigOption configOption = null;
    try {
      configOption = Config.load(fileConfig.getConfigFile(), SM_TYPE);
    } catch (ConfigException e) {
      logger.error("load configure failed, exception: {}", e);
      return null;
    }

    return new BcosSDK(configOption);
  }

  @Bean
  public Client client(@Qualifier(value = "bcosSDK") BcosSDK bcosSDK) {
    return bcosSDK.getClient(groupId);
  }

  @Bean
  public TransactionDecoderService transactionDecoderService(
      @Qualifier(value = "client") Client client) {
    return new TransactionDecoderService(client.getCryptoSuite());
  }
}
