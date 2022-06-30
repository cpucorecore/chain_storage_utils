package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.constants.Response;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {
  @Value("${passwd}")
  private String passwd;

  @Autowired private Client client;

  @PostMapping("create")
  public RespBody<String> handleCreate() {
    CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().createKeyPair();
    cryptoKeyPair.storeKeyPairWithP12Format(passwd);
    return new RespBody<>(Response.SUCCESS, cryptoKeyPair.getAddress());
  }
}
