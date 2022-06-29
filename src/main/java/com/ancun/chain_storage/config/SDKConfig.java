package com.ancun.chain_storage.config;

import static org.fisco.bcos.sdk.model.CryptoType.SM_TYPE;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.config.Config;
import org.fisco.bcos.sdk.config.ConfigOption;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SDKConfig {
  Logger logger = LoggerFactory.getLogger(SDKConfig.class);
  @Autowired private FileConfig fileConfig;

  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
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
}
