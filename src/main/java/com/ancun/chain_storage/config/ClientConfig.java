package com.ancun.chain_storage.config;

import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
  @Value("${chain.groupId}")
  private Integer groupId;

  @Autowired private BcosSDK bcosSDK;

  @Bean
  public Client client() {
    return bcosSDK.getClient(groupId);
  }
}
