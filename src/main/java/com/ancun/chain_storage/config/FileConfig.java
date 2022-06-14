package com.ancun.chain_storage.config;

import org.fisco.bcos.sdk.model.ConstantConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan
public class FileConfig {
  @Bean
  public String getConfigFile() {
    try {
      ClassPathResource classPathResource = new ClassPathResource(ConstantConfig.CONFIG_FILE_NAME);
      System.out.println(classPathResource.getFile().getPath());
      return classPathResource.getFile().getPath();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
