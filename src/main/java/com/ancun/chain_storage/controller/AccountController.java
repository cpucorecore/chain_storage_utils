package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.constants.ResponseInfo;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_account.AccountService;
import com.ancun.chain_storage.service_account.impl.ChainAccount;
import java.io.IOException;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {
  Logger logger = LoggerFactory.getLogger(AccountController.class);

  @Resource private AccountService accountService;

  @PostMapping("create/{passwd}")
  public RespBody<String> handleCreate(
      @PathVariable String passwd) { // TODO passwd should not transfer by url
    ChainAccount chainAccount = null;
    try {
      chainAccount = accountService.createChainAccount(passwd);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new RespBody<>(ResponseInfo.SUCCESS, chainAccount.getCryptoKeyPair().getAddress());
  }
}
