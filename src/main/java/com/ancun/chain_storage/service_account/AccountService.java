package com.ancun.chain_storage.service_account;

import com.ancun.chain_storage.service_account.impl.ChainAccount;
import java.io.IOException;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;

public interface AccountService {
  ChainAccount createChainAccount(String password) throws IOException;

  ChainAccount getChainAccount(String address, String password) throws Exception;

  CryptoKeyPair getReadonlyKeyPair();
}
