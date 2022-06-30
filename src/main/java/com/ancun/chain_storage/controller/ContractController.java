package com.ancun.chain_storage.controller;

import static com.ancun.chain_storage.constants.ContractName.AdminBytes32;
import static com.ancun.chain_storage.constants.ContractName.BlacklistBytes32;
import static com.ancun.chain_storage.constants.ContractName.ChainStorageBytes32;
import static com.ancun.chain_storage.constants.ContractName.FileManagerBytes32;
import static com.ancun.chain_storage.constants.ContractName.FileStorageBytes32;
import static com.ancun.chain_storage.constants.ContractName.NodeManagerBytes32;
import static com.ancun.chain_storage.constants.ContractName.NodeStorageBytes32;
import static com.ancun.chain_storage.constants.ContractName.SettingBytes32;
import static com.ancun.chain_storage.constants.ContractName.UserManagerBytes32;
import static com.ancun.chain_storage.constants.ContractName.UserStorageBytes32;
import static com.ancun.chain_storage.constants.Response.SUCCESS;

import com.ancun.chain_storage.config.ResolverConfig;
import com.ancun.chain_storage.contracts.Blacklist;
import com.ancun.chain_storage.contracts.ChainStorage;
import com.ancun.chain_storage.contracts.FileManager;
import com.ancun.chain_storage.contracts.FileStorage;
import com.ancun.chain_storage.contracts.NodeManager;
import com.ancun.chain_storage.contracts.NodeStorage;
import com.ancun.chain_storage.contracts.Resolver;
import com.ancun.chain_storage.contracts.Setting;
import com.ancun.chain_storage.contracts.SettingStorage;
import com.ancun.chain_storage.contracts.UserManager;
import com.ancun.chain_storage.contracts.UserStorage;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contract")
public class ContractController {
  private Logger logger = LoggerFactory.getLogger(ContractController.class);

  @Value("${passwd}")
  private String passwd;

  @Value("${settings.MaxCidLen}")
  private BigInteger MaxCidLen;

  @Value("${settings.MaxNodeExtLen}")
  private BigInteger MaxNodeExtLen;

  @Value("${settings.MaxUserExtLen}")
  private BigInteger MaxUserExtLen;

  @Value("${settings.MaxFileExtLen}")
  private BigInteger MaxFileExtLen;

  @Value("${settings.MaxNodeCanAddFile}")
  private BigInteger MaxNodeCanAddFile;

  @Value("${settings.MaxNodeCanDeleteFile}")
  private BigInteger MaxNodeCanDeleteFile;

  @Value("${settings.Replica}")
  private BigInteger Replica;

  @Value("${settings.UserInitSpace}")
  private BigInteger UserInitSpace;

  @Autowired private Client client;

  @Autowired private ResolverConfig resolverConfig;

  @PutMapping("resolver/{address}")
  public RespBody<String> setResolver(@PathVariable(value = "address") String address) {
    resolverConfig.setResolverAddress(address);
    return new RespBody<>(SUCCESS);
  }

  @GetMapping("resolver")
  public RespBody<String> getResolver() {
    RespBody<String> response = new RespBody<>(SUCCESS, resolverConfig.getResolverAddress());
    return response;
  }

  @PostMapping("/deploy/{deployer_address}")
  public RespBody<String> deployContract(
      @PathVariable(value = "deployer_address") String deployerAddress) {
    String deployerP12FilePath =
        client.getCryptoSuite().getCryptoKeyPair().getP12KeyStoreFilePath(deployerAddress);
    client.getCryptoSuite().loadAccount("p12", deployerP12FilePath, passwd);
    CryptoKeyPair keyPair = client.getCryptoSuite().getCryptoKeyPair();

    Map<String, String> contractAddresses = new HashMap<>();
    do {
      Resolver resolver = null;
      try {
        resolver = Resolver.deploy(client, keyPair);
      } catch (ContractException e) {
        logger.error("Resolver deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("resolver", resolver.getContractAddress());
      logger.info("Resolver deploy finish: {}", resolver.getContractAddress());

      Setting setting = null;
      try {
        setting = Setting.deploy(client, keyPair);
      } catch (ContractException e) {
        logger.error("Setting deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("setting", setting.getContractAddress());
      logger.info("Setting deploy finish: {}", setting.getContractAddress());

      SettingStorage settingStorage = null;
      try {
        settingStorage = SettingStorage.deploy(client, keyPair, setting.getContractAddress());
      } catch (ContractException e) {
        logger.error("SettingStorage deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("settingStorage", settingStorage.getContractAddress());

      TransactionReceipt receipt = setting.setStorage(settingStorage.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("setting.setStorage failed: {}", receipt.getMessage());
        return null;
      }

      receipt = resolver.setAddress(SettingBytes32, setting.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(Setting) failed: {}", receipt.getMessage());
        return null;
      }

      // Blacklist
      Blacklist blacklist = null;
      try {
        blacklist = Blacklist.deploy(client, keyPair, resolver.getContractAddress());
      } catch (ContractException e) {
        logger.error("Blacklist deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("blacklist", blacklist.getContractAddress());
      logger.info("blacklist deploy finish: {}", blacklist.getContractAddress());

      receipt = resolver.setAddress(BlacklistBytes32, blacklist.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(Blacklist) failed: {}", receipt.getMessage());
        return null;
      }

      // File and FileStorage
      FileManager fileManager = null;
      try {
        fileManager = FileManager.deploy(client, keyPair, resolver.getContractAddress());
      } catch (ContractException e) {
        logger.error("FileManager deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("fileManager", fileManager.getContractAddress());
      logger.info("FileManager deploy finish: {}", fileManager.getContractAddress());

      FileStorage fileStorage = null;
      try {
        fileStorage = FileStorage.deploy(client, keyPair, fileManager.getContractAddress());
      } catch (ContractException e) {
        logger.error("FileStorage deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("fileStorage", fileStorage.getContractAddress());
      logger.info("FileStorage deploy finish: {}", fileStorage.getContractAddress());

      receipt = fileManager.setStorage(fileStorage.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("fileManager.setStorage failed: {}", receipt.getMessage());
        return null;
      }

      receipt = resolver.setAddress(FileManagerBytes32, fileManager.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(File) failed: {}", receipt.getMessage());
        return null;
      }

      receipt = resolver.setAddress(FileStorageBytes32, fileStorage.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(FileStorage) failed: {}", receipt.getMessage());
        return null;
      }

      // User and UserStorage
      UserManager userManager = null;
      try {
        userManager = UserManager.deploy(client, keyPair, resolver.getContractAddress());
      } catch (ContractException e) {
        logger.error("UserManager deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("userManager", userManager.getContractAddress());
      logger.info("UserManager deploy finish: {}", userManager.getContractAddress());

      UserStorage userStorage = null;
      try {
        userStorage = UserStorage.deploy(client, keyPair, userManager.getContractAddress());
      } catch (ContractException e) {
        logger.error("UserStorage deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("userStorage", userStorage.getContractAddress());
      logger.info("UserStorage deploy finish: {}", userStorage.getContractAddress());

      receipt = userManager.setStorage(userStorage.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("userManager.setStorage failed: {}", receipt.getMessage());
        return null;
      }

      receipt = resolver.setAddress(UserManagerBytes32, userManager.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(User) failed: {}", receipt.getMessage());
        return null;
      }

      receipt = resolver.setAddress(UserStorageBytes32, userStorage.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(UserStorage) failed: {}", receipt.getMessage());
        return null;
      }

      // Node and NodeStorage
      NodeManager nodeManager = null;
      try {
        nodeManager = NodeManager.deploy(client, keyPair, resolver.getContractAddress());
      } catch (ContractException e) {
        logger.error("NodeManager deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("nodeManager", nodeManager.getContractAddress());
      logger.info("NodeManager deploy finish: {}", nodeManager.getContractAddress());

      NodeStorage nodeStorage = null;
      try {
        nodeStorage = NodeStorage.deploy(client, keyPair, nodeManager.getContractAddress());
      } catch (ContractException e) {
        logger.error("NodeStorage deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("nodeStorage", nodeStorage.getContractAddress());
      logger.info("NodeStorage deploy finish: {}", nodeStorage.getContractAddress());

      receipt = nodeManager.setStorage(nodeStorage.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("nodeManager.setStorage failed: {}", receipt.getMessage());
        return null;
      }

      receipt = resolver.setAddress(NodeManagerBytes32, nodeManager.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(Node) failed: {}", receipt.getMessage());
        return null;
      }

      receipt = resolver.setAddress(NodeStorageBytes32, nodeStorage.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(NodeStorage) failed: {}", receipt.getMessage());
        return null;
      }

      receipt = resolver.setAddress(AdminBytes32, keyPair.getAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(UserStorage) failed: {}", receipt.getMessage());
        return null;
      }

      // ChainStorage
      ChainStorage chainStorage = null;
      try {
        chainStorage = ChainStorage.deploy(client, keyPair);
      } catch (ContractException e) {
        logger.error("ChainStorage deploy failed: {}", e.getMessage());
        return null;
      }
      contractAddresses.put("chainStorage", chainStorage.getContractAddress());
      receipt = resolver.setAddress(ChainStorageBytes32, chainStorage.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("resolver.setAddress(ChainStorage) failed: {}", receipt.getMessage());
        return null;
      }

      logger.info("ChainStorage deploy finish: {}", chainStorage.getContractAddress());

      receipt = chainStorage.initialize(resolver.getContractAddress());
      if (!receipt.isStatusOK()) {
        logger.error("ChainStorage initialize failed: {}", receipt.getMessage());
        return null;
      }
      logger.info("chain storage init finish");

      // refresh cache
      receipt = fileManager.refreshCache();
      if (!receipt.isStatusOK()) {
        logger.error("fileManager refresh cache failed: {}", receipt.getMessage());
        return null;
      }
      logger.info("fileManager refreshCache finish");

      receipt = userManager.refreshCache();
      if (!receipt.isStatusOK()) {
        logger.error("userManager refresh cache failed: {}", receipt.getMessage());
        return null;
      }
      logger.info("userManager refreshCache finish");

      receipt = nodeManager.refreshCache();
      if (!receipt.isStatusOK()) {
        logger.error("nodeManager refresh cache failed: {}", receipt.getMessage());
        return null;
      }
      logger.info("nodeManager refreshCache finish");

      receipt = blacklist.refreshCache();
      if (!receipt.isStatusOK()) {
        logger.error("blacklist refresh cache failed: {}", receipt.getMessage());
        return null;
      }
      logger.info("blacklist refreshCache finish");

      receipt = chainStorage.refreshCache();
      if (!receipt.isStatusOK()) {
        logger.error("chainStorage refresh cache failed: {}", receipt.getMessage());
        return null;
      }
      logger.info("chainStorage refreshCache finish");

      // setup Settings
      receipt = setting.setReplica(Replica);
      if (!receipt.isStatusOK()) {
        logger.error("setting setReplica failed: {}", receipt.getMessage());
        return null;
      }
      receipt = setting.setMaxNodeExtLength(MaxNodeExtLen);
      if (!receipt.isStatusOK()) {
        logger.error("setting setMaxNodeExtLength failed: {}", receipt.getMessage());
        return null;
      }
      receipt = setting.setMaxUserExtLength(MaxUserExtLen);
      if (!receipt.isStatusOK()) {
        logger.error("setting setMaxUserExtLength failed: {}", receipt.getMessage());
        return null;
      }
      receipt = setting.setMaxFileExtLength(MaxFileExtLen);
      if (!receipt.isStatusOK()) {
        logger.error("setting setMaxFileExtLength failed: {}", receipt.getMessage());
        return null;
      }
      receipt = setting.setMaxCidLength(MaxCidLen);
      if (!receipt.isStatusOK()) {
        logger.error("setting setMaxCidLength failed: {}", receipt.getMessage());
        return null;
      }
      receipt = setting.setInitSpace(UserInitSpace);
      if (!receipt.isStatusOK()) {
        logger.error("setting setInitSpace failed: {}", receipt.getMessage());
        return null;
      }
      receipt = setting.setMaxNodeCanAddFileCount(MaxNodeCanAddFile);
      if (!receipt.isStatusOK()) {
        logger.error("setting setMaxNodeCanAddFileCount failed: {}", receipt.getMessage());
        return null;
      }
      receipt = setting.setMaxNodeCanDeleteFileCount(MaxNodeCanDeleteFile);
      if (!receipt.isStatusOK()) {
        logger.error("setting setMaxNodeCanDeleteFileCount failed: {}", receipt.getMessage());
        return null;
      }
      logger.info("setting setup finish");

      resolverConfig.setResolverAddress(resolver.getContractAddress());
    } while (false);

    return new RespBody<>(SUCCESS, contractAddresses.toString());
  }
}
