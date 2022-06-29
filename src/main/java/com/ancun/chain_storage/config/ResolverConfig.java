package com.ancun.chain_storage.config;

import com.ancun.chain_storage.contracts.Resolver;
import org.fisco.bcos.sdk.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResolverConfig {
  @Value("${resolverAddress}")
  private String resolverAddress;

  @Autowired private Client client;

  @Bean
  public Resolver resolver() {
    return Resolver.load(resolverAddress, client, client.getCryptoSuite().getCryptoKeyPair());
  }

  public Resolver resolver(String address) {
    return Resolver.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
  }
}
