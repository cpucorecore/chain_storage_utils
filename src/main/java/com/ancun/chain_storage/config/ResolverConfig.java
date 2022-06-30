package com.ancun.chain_storage.config;

import com.ancun.chain_storage.contracts.Resolver;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(SDKConfig.class)
public class ResolverConfig {
  Logger logger = LoggerFactory.getLogger(ResolverConfig.class);

  @Value("${resolverAddress}")
  private String resolverAddress;

  @Autowired private Client client;

  private Resolver resolver;

  public String getResolverAddress() {
    return resolverAddress;
  }

  public void setResolverAddress(String resolverAddress) {
    this.resolverAddress = resolverAddress;
  }

  public String getAddress(byte[] name) {
    if (null == resolver || !resolver.getContractAddress().equals(resolverAddress)) {
      resolver = Resolver.load(resolverAddress, client, client.getCryptoSuite().getCryptoKeyPair());
    }

    if (null == resolver) {
      logger.error("resolver is null");
      return null;
    }

    try {
      logger.debug("resolver[{}].getAddress({})", resolver.getContractAddress(), name);
      return resolver.getAddress(name);
    } catch (ContractException e) {
      logger.error("resolver.getAddress({}) failed: {}", name, e);
      return null;
    }
  }
}
