package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.math.BigInteger;

public class RequestUtils {
  public static final String keyAddress = "address";
  public static final String keyPassword = "password";

  public static final int ADDRESS_LENGTH = 42;
  public static final int TX_ID_LENGTH = 66;
  public static final int MAX_PASSWORD_LENGTH = 64;

  public static ChainAccountInfo parseChainAccountInfo(String accountInfo) throws Exception {
    JSONObject json = JSON.parseObject(accountInfo);
    String address = json.getString(keyAddress);
    String password = json.getString(keyPassword);

    if (!checkAddress(address)) {
      throw new Exception("invalid address:[" + address + "]");
    }
    if (!checkPassword(password)) {
      throw new Exception("invalid password:[" + password + "]");
    }

    ChainAccountInfo chainAccountInfo = new ChainAccountInfo(address, password);
    return chainAccountInfo;
  }

  public static Boolean checkPassword(String password) {
    if (null == password || "".equals(password)) {
      return false;
    }

    return password.length() <= MAX_PASSWORD_LENGTH;
  }

  public static Boolean checkAddress(String address) {
    if (null == address || "".equals(address)) {
      return false;
    }

    return address.length() == ADDRESS_LENGTH;
  }

  public static Boolean checkTxId(String txId) {
    if (null == txId || "".equals(txId)) {
      return false;
    }

    return txId.length() == TX_ID_LENGTH;
  }

  public static Boolean nonNegative(BigInteger value) {
    return (1 == value.compareTo(BigInteger.valueOf(-1)));
  }
}
