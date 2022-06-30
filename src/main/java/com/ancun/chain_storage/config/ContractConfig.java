package com.ancun.chain_storage.config;

import static com.ancun.chain_storage.constants.ContractName.ChainStorageBytes32;
import static com.ancun.chain_storage.constants.ContractName.FileStorageBytes32;
import static com.ancun.chain_storage.constants.ContractName.NodeStorageBytes32;
import static com.ancun.chain_storage.constants.ContractName.SettingBytes32;
import static com.ancun.chain_storage.constants.ContractName.UserStorageBytes32;

import com.ancun.chain_storage.contracts.ChainStorage;
import com.ancun.chain_storage.contracts.FileStorage;
import com.ancun.chain_storage.contracts.NodeStorage;
import com.ancun.chain_storage.contracts.Setting;
import com.ancun.chain_storage.contracts.UserStorage;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(ResolverConfig.class)
public class ContractConfig {
  @Autowired private ResolverConfig resolverConfig;

  @Autowired private Client client;

  private ChainStorage chainStorage;
  private Setting setting;
  private NodeStorage nodeStorage;
  private FileStorage fileStorage;
  private UserStorage userStorage;

  public ChainStorage chainStorage(CryptoKeyPair keyPair) {
    String address = resolverConfig.getAddress(ChainStorageBytes32);
    if (null == chainStorage
        || !chainStorage.getContractAddress().equals(address)
        || !chainStorage.getCurrentExternalAccountAddress().equals(keyPair.getAddress())) {
      chainStorage = ChainStorage.load(address, client, keyPair);
    }

    return chainStorage;
  }

  public Setting setting() {
    String address = resolverConfig.getAddress(SettingBytes32);
    if (null == setting || !setting.getContractAddress().equals(address)) {
      setting = Setting.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    }

    return setting;
  }

  public Setting setting(CryptoKeyPair keyPair) {
    String address = resolverConfig.getAddress(SettingBytes32);
    if (null == setting
        || !setting.getContractAddress().equals(address)
        || !setting.getCurrentExternalAccountAddress().equals(keyPair.getAddress())) {
      setting = Setting.load(address, client, keyPair);
    }

    return setting;
  }

  public NodeStorage nodeStorage() {
    String address = resolverConfig.getAddress(NodeStorageBytes32);
    if (null == nodeStorage || !nodeStorage.getContractAddress().equals(address)) {
      nodeStorage = NodeStorage.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    }
    return nodeStorage;
  }

  public UserStorage userStorage() {
    String address = resolverConfig.getAddress(UserStorageBytes32);
    if (null == userStorage || !userStorage.getContractAddress().equals(address)) {
      userStorage = UserStorage.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    }
    return userStorage;
  }

  public FileStorage fileStorage() {
    String address = resolverConfig.getAddress(FileStorageBytes32);
    if (null == fileStorage || !fileStorage.getContractAddress().equals(address)) {
      fileStorage = FileStorage.load(address, client, client.getCryptoSuite().getCryptoKeyPair());
    }
    return fileStorage;
  }
}
