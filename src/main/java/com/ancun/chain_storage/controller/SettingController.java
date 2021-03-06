package com.ancun.chain_storage.controller;

import static com.ancun.chain_storage.constants.Response.CALL_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.Response.SUCCESS;
import static com.ancun.chain_storage.constants.Response.UNKNOWN_SETTING_KEY;

import com.ancun.chain_storage.config.ContractConfig;
import com.ancun.chain_storage.config.KeyPairLoader;
import com.ancun.chain_storage.constants.Response;
import com.ancun.chain_storage.contracts.Setting;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chain_storage/setting")
public class SettingController {
  public static final String Replica = "Replica";
  public static final String InitSpace = "InitSpace";
  public static final String AdminAccount = "AdminAccount";
  public static final String MaxUserExtLength = "MaxUserExtLength";
  public static final String MaxNodeExtLength = "MaxNodeExtLength";
  public static final String MaxFileExtLength = "MaxFileExtLength";
  public static final String MaxCidLength = "MaxCidLength";
  public static final String MaxNodeCanAddFileCount = "MaxNodeCanAddFileCount";
  public static final String MaxNodeCanDeleteFileCount = "MaxNodeCanDeleteFileCount";

  @Autowired private KeyPairLoader keyPairLoader;
  @Autowired private ContractConfig contractConfig;

  @PutMapping("/{deployerAddress}/{key}/{value}")
  public RespBody<String> set(
      @PathVariable(value = "deployerAddress") String deployerAddress,
      @PathVariable(value = "key") String key,
      @PathVariable(value = "value") String value) {

    CryptoKeyPair keyPair = keyPairLoader.loadKeyPair(deployerAddress);
    if (null == keyPair) {
      return new RespBody<>(Response.LOAD_CHAIN_ACCOUNT_FAILED, deployerAddress);
    }
    Setting setting = contractConfig.setting(keyPair);

    TransactionReceipt receipt = null;
    BigInteger bigIntegerValue = BigInteger.ZERO;
    if (!AdminAccount.equals(key)) {
      long lvalue;
      try {
        lvalue = Long.parseLong(value);
      } catch (NumberFormatException e) {
        return new RespBody<>(Response.INVALID_REQUEST, String.format("wrong value: [%s]", value));
      }
      bigIntegerValue = BigInteger.valueOf(lvalue);
    }

    switch (key) {
      case Replica:
        receipt = setting.setReplica(bigIntegerValue);
        break;
      case InitSpace:
        receipt = setting.setInitSpace(bigIntegerValue);
        break;
      case AdminAccount:
        receipt = setting.setAdmin(value);
        break;
      case MaxUserExtLength:
        receipt = setting.setMaxUserExtLength(bigIntegerValue);
        break;
      case MaxNodeExtLength:
        receipt = setting.setMaxNodeExtLength(bigIntegerValue);
        break;
      case MaxFileExtLength:
        receipt = setting.setMaxFileExtLength(bigIntegerValue);
        break;
      case MaxCidLength:
        receipt = setting.setMaxCidLength(bigIntegerValue);
        break;
      case MaxNodeCanAddFileCount:
        receipt = setting.setMaxNodeCanAddFileCount(bigIntegerValue);
        break;
      case MaxNodeCanDeleteFileCount:
        receipt = setting.setMaxNodeCanDeleteFileCount(bigIntegerValue);
        break;
      default:
        log.error("unknown setting key:{}", key);
        return new RespBody<>(UNKNOWN_SETTING_KEY, String.format("invalid key:[%s]", key));
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @GetMapping("/{key}")
  public RespBody<String> get(@PathVariable(value = "key") String key) {
    Setting setting = contractConfig.setting();

    BigInteger value = BigInteger.ZERO;
    String admin = "";
    try {
      switch (key) {
        case Replica:
          value = setting.getReplica();
          break;
        case InitSpace:
          value = setting.getInitSpace();
          break;
        case AdminAccount:
          admin = setting.getAdmin();
          break;
        case MaxUserExtLength:
          value = setting.getMaxUserExtLength();
          break;
        case MaxNodeExtLength:
          value = setting.getMaxNodeExtLength();
          break;
        case MaxFileExtLength:
          value = setting.getMaxFileExtLength();
          break;
        case MaxCidLength:
          value = setting.getMaxCidLength();
          break;
        case MaxNodeCanAddFileCount:
          value = setting.getMaxNodeCanAddFileCount();
          break;
        case MaxNodeCanDeleteFileCount:
          value = setting.getMaxNodeCanDeleteFileCount();
          break;
        default:
          log.error("unknown setting key:{}", key);
          return new RespBody<>(UNKNOWN_SETTING_KEY, String.format("invalid key:[%s]", key));
      }
    } catch (ContractException e) {
      return new RespBody<>(CALL_CONTRACT_FAILED);
    }

    if (key.equals(AdminAccount)) {
      return new RespBody<>(SUCCESS, admin);
    } else {
      return new RespBody<>(SUCCESS, value.toString());
    }
  }
}
