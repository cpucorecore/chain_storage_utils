package com.ancun.chain_storage.service_blockchain.impl;

import com.ancun.chain_storage.config.FileConfig;
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
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BlockchainServiceImpl implements BlockchainService {

  public static final String CN_Resolver = "Resolver";
  public static final String CN_Setting = "Setting";
  public static final String CN_Blacklist = "Blacklist";
  public static final String CN_ChainStorage = "ChainStorage";
  public static final String CN_NodeManager = "NodeManager";
  public static final String CN_NodeStorage = "NodeStorage";
  public static final String CN_UserManager = "UserManager";
  public static final String CN_UserStorage = "UserStorage";
  public static final String CN_FileManager = "FileManager";
  public static final String CN_FileStorage = "FileStorage";
  public static final String Account_Admin = "Admin";
  public static final byte[] ResolverBytes32 = String2SolidityBytes32(CN_Resolver);
  public static final byte[] SettingBytes32 = String2SolidityBytes32(CN_Setting);
  public static final byte[] BlacklistBytes32 = String2SolidityBytes32(CN_Blacklist);
  public static final byte[] ChainStorageBytes32 = String2SolidityBytes32(CN_ChainStorage);
  public static final byte[] NodeManagerBytes32 = String2SolidityBytes32(CN_NodeManager);
  public static final byte[] NodeStorageBytes32 = String2SolidityBytes32(CN_NodeStorage);
  public static final byte[] UserManagerBytes32 = String2SolidityBytes32(CN_UserManager);
  public static final byte[] UserStorageBytes32 = String2SolidityBytes32(CN_UserStorage);
  public static final byte[] FileManagerBytes32 = String2SolidityBytes32(CN_FileManager);
  public static final byte[] FileStorageBytes32 = String2SolidityBytes32(CN_FileStorage);

  public static final byte[] AdminBytes32 = String2SolidityBytes32(Account_Admin);

  private static final Logger logger = LoggerFactory.getLogger(BlockchainServiceImpl.class);

  @Resource private FileConfig fileConfig;

  @Value("${chain.groupId}")
  private int groupId;

  @Value("${resolverAddress}")
  private String resolverAddress;

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

  private Client client;
  private BcosSDK sdk;
  private CryptoKeyPair keyPairForRead;
  private Map<String, byte[]> contractNames = new HashMap<>();
  private TransactionDecoderInterface decoder;

  private void sdkClientInstance() {
    String path = fileConfig.getConfigFile();
    if (null == sdk) {
      try {
        sdk = BcosSDK.build(path);
      } catch (Exception e) {
        sdk = BcosSDK.build(path);
      }
    }
    if (null == client) {
      client = sdk.getClient(groupId);

      keyPairForRead = client.getCryptoSuite().createKeyPair();

      contractNames.put(CN_Resolver, ResolverBytes32);
      contractNames.put(CN_Setting, SettingBytes32);
      contractNames.put(CN_Blacklist, BlacklistBytes32);
      contractNames.put(CN_ChainStorage, ChainStorageBytes32);
      contractNames.put(CN_NodeManager, NodeManagerBytes32);
      contractNames.put(CN_NodeStorage, NodeStorageBytes32);
      contractNames.put(CN_UserManager, UserManagerBytes32);
      contractNames.put(CN_UserStorage, UserStorageBytes32);
      contractNames.put(CN_FileManager, FileManagerBytes32);
      contractNames.put(CN_FileStorage, FileStorageBytes32);
    }
    if (null == decoder) {
      decoder = new TransactionDecoderService(client.getCryptoSuite());
    }
  }

  public TransactionDecoderInterface getDecoder() {
    if (null == decoder) {
      sdkClientInstance();
    }
    return decoder;
  }

  public static byte[] String2SolidityBytes32(String value) {
    byte[] valueBytes = value.getBytes();
    byte[] targetBytes = new byte[32];

    for (int i = 0; i < valueBytes.length; i++) {
      targetBytes[i] = valueBytes[i];
    }
    return targetBytes;
  }

  public Map<String, String> deployCSContracts(CryptoKeyPair keyPair) throws Exception {
    sdkClientInstance();
    Map<String, String> contractAddresses = new HashMap<>();

    Resolver resolver = Resolver.deploy(client, keyPair);
    contractAddresses.put("resolver", resolver.getContractAddress());
    logger.info("resolver deploy finish: {}", resolver.getContractAddress());

    Setting setting = Setting.deploy(client, keyPair);
    contractAddresses.put("setting", setting.getContractAddress());
    logger.info("setting deploy finish: {}", setting.getContractAddress());

    SettingStorage settingStorage =
        SettingStorage.deploy(client, keyPair, setting.getContractAddress());
    contractAddresses.put("settingStorage", settingStorage.getContractAddress());

    TransactionReceipt receipt = setting.setStorage(settingStorage.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("setting.setStorage failed: " + receipt.getMessage());
    }

    receipt = resolver.setAddress(SettingBytes32, setting.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(Setting) failed: " + receipt.getMessage());
    }

    // Blacklist
    Blacklist blacklist = Blacklist.deploy(client, keyPair, resolver.getContractAddress());
    contractAddresses.put("blacklist", blacklist.getContractAddress());
    logger.info("blacklist deploy finish: {}", blacklist.getContractAddress());

    receipt = resolver.setAddress(BlacklistBytes32, blacklist.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(Blacklist) failed: " + receipt.getMessage());
    }

    // File and FileStorage
    FileManager fileManager = FileManager.deploy(client, keyPair, resolver.getContractAddress());
    contractAddresses.put("fileManager", fileManager.getContractAddress());
    logger.info("FileManager deploy finish: {}", fileManager.getContractAddress());

    FileStorage fileStorage = FileStorage.deploy(client, keyPair, fileManager.getContractAddress());
    contractAddresses.put("fileStorage", fileStorage.getContractAddress());
    logger.info("FileStorage deploy finish: {}", fileStorage.getContractAddress());

    receipt = fileManager.setStorage(fileStorage.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("fileManager.setStorage failed: " + receipt.getMessage());
    }

    receipt = resolver.setAddress(FileManagerBytes32, fileManager.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(File) failed: " + receipt.getMessage());
    }

    receipt = resolver.setAddress(FileStorageBytes32, fileStorage.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(FileStorage) failed: " + receipt.getMessage());
    }

    // User and UserStorage
    UserManager userManager = UserManager.deploy(client, keyPair, resolver.getContractAddress());
    contractAddresses.put("userManager", userManager.getContractAddress());
    logger.info("UserManager deploy finish: {}", userManager.getContractAddress());

    UserStorage userStorage = UserStorage.deploy(client, keyPair, userManager.getContractAddress());
    contractAddresses.put("userStorage", userStorage.getContractAddress());
    logger.info("UserStorage deploy finish: {}", userStorage.getContractAddress());

    receipt = userManager.setStorage(userStorage.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("userManager.setStorage failed: " + receipt.getMessage());
    }

    receipt = resolver.setAddress(UserManagerBytes32, userManager.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(User) failed: " + receipt.getMessage());
    }

    receipt = resolver.setAddress(UserStorageBytes32, userStorage.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(UserStorage) failed: " + receipt.getMessage());
    }

    // Node and NodeStorage
    NodeManager nodeManager = NodeManager.deploy(client, keyPair, resolver.getContractAddress());
    contractAddresses.put("nodeManager", nodeManager.getContractAddress());
    logger.info("NodeManager deploy finish: {}", nodeManager.getContractAddress());

    NodeStorage nodeStorage = NodeStorage.deploy(client, keyPair, nodeManager.getContractAddress());
    contractAddresses.put("nodeStorage", nodeStorage.getContractAddress());
    logger.info("NodeStorage deploy finish: {}", nodeStorage.getContractAddress());

    receipt = nodeManager.setStorage(nodeStorage.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("nodeManager.setStorage failed: " + receipt.getMessage());
    }

    receipt = resolver.setAddress(NodeManagerBytes32, nodeManager.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(Node) failed: " + receipt.getMessage());
    }

    receipt = resolver.setAddress(NodeStorageBytes32, nodeStorage.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(NodeStorage) failed: " + receipt.getMessage());
    }

    receipt = resolver.setAddress(AdminBytes32, keyPair.getAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(UserStorage) failed: " + receipt.getMessage());
    }

    // ChainStorage
    ChainStorage chainStorage = ChainStorage.deploy(client, keyPair);
    contractAddresses.put("chainStorage", chainStorage.getContractAddress());
    receipt = resolver.setAddress(ChainStorageBytes32, chainStorage.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("resolver.setAddress(ChainStorage) failed: " + receipt.getMessage());
    }

    logger.info("ChainStorage deploy finish: {}", chainStorage.getContractAddress());

    receipt = chainStorage.initialize(resolver.getContractAddress());
    if (!receipt.isStatusOK()) {
      throw new Exception("ChainStorage initialize failed: " + receipt.getMessage());
    }
    logger.info("chain storage init finish");

    // refresh cache
    receipt = fileManager.refreshCache();
    if (!receipt.isStatusOK()) {
      throw new Exception("fileManager refresh cache failed: " + receipt.getMessage());
    }
    logger.info("fileManager refreshCache finish");

    receipt = userManager.refreshCache();
    if (!receipt.isStatusOK()) {
      throw new Exception("userManager refresh cache failed: " + receipt.getMessage());
    }
    logger.info("userManager refreshCache finish");

    receipt = nodeManager.refreshCache();
    if (!receipt.isStatusOK()) {
      throw new Exception("nodeManager refresh cache failed: " + receipt.getMessage());
    }
    logger.info("nodeManager refreshCache finish");

    receipt = blacklist.refreshCache();
    if (!receipt.isStatusOK()) {
      throw new Exception("blacklist refresh cache failed: " + receipt.getMessage());
    }
    logger.info("blacklist refreshCache finish");

    receipt = chainStorage.refreshCache();
    if (!receipt.isStatusOK()) {
      throw new Exception("chainStorage refresh cache failed: " + receipt.getMessage());
    }
    logger.info("chainStorage refreshCache finish");

    // setup Settings
    receipt = setting.setReplica(Replica);
    if (!receipt.isStatusOK()) {
      throw new Exception("setting setReplica failed: " + receipt.getMessage());
    }
    receipt = setting.setMaxNodeExtLength(MaxNodeExtLen);
    if (!receipt.isStatusOK()) {
      throw new Exception("setting setMaxNodeExtLength failed: " + receipt.getMessage());
    }
    receipt = setting.setMaxUserExtLength(MaxUserExtLen);
    if (!receipt.isStatusOK()) {
      throw new Exception("setting setMaxUserExtLength failed: " + receipt.getMessage());
    }
    receipt = setting.setMaxFileExtLength(MaxFileExtLen);
    if (!receipt.isStatusOK()) {
      throw new Exception("setting setMaxFileExtLength failed: " + receipt.getMessage());
    }
    receipt = setting.setMaxCidLength(MaxCidLen);
    if (!receipt.isStatusOK()) {
      throw new Exception("setting setMaxCidLength failed: " + receipt.getMessage());
    }
    receipt = setting.setInitSpace(UserInitSpace);
    if (!receipt.isStatusOK()) {
      throw new Exception("setting setInitSpace failed: " + receipt.getMessage());
    }
    receipt = setting.setMaxNodeCanAddFileCount(MaxNodeCanAddFile);
    if (!receipt.isStatusOK()) {
      throw new Exception("setting setMaxNodeCanAddFileCount failed: " + receipt.getMessage());
    }
    receipt = setting.setMaxNodeCanDeleteFileCount(MaxNodeCanDeleteFile);
    if (!receipt.isStatusOK()) {
      throw new Exception("setting setMaxNodeCanDeleteFileCount failed: " + receipt.getMessage());
    }
    logger.info("setting setup finish");

    resolverAddress = contractAddresses.get("resolver");

    return contractAddresses;
  }

  public void setResolverAddress(String address) {
    resolverAddress = address;
  }

  public String getResolverAddress() {
    return resolverAddress;
  }

  public String getContractAddress(String contractName) throws ContractException {
    Resolver resolver = loadResolverContract();
    String address = resolver.getAddress(contractNames.get(contractName));
    logger.debug("resolver.getAddress(\"{}\"):{}", contractName, address);
    return address;
  }

  public Resolver loadResolverContract() throws ContractException {
    sdkClientInstance();

    if (null == resolverAddress || "".equals(resolverAddress)) {
      throw new ContractException("invalid resolver address");
    }

    Resolver resolver = Resolver.load(resolverAddress, client, keyPairForRead);
    String chainStorageAddress = resolver.getAddress(ChainStorageBytes32);
    logger.debug(
        "loadResolverContract() try: Resolver.getAddress(\"ChainStorage\"):{}",
        chainStorageAddress);
    return resolver;
  }

  public Resolver loadResolverContract(String contractAddress) throws ContractException {
    setResolverAddress(contractAddress);
    return loadResolverContract();
  }

  public ChainStorage loadChainStorageContract(CryptoKeyPair keyPair, String contractAddress) {
    sdkClientInstance();
    ChainStorage chainStorage = ChainStorage.load(contractAddress, client, keyPair);
    return chainStorage;
  }

  public Setting loadSettingContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException {
    sdkClientInstance();
    Setting setting = Setting.load(contractAddress, client, keyPair);
    String admin = setting.getAdmin();
    logger.debug("Setting.getAdmin():{}", admin);
    return setting;
  }

  public NodeManager loadNodeManagerContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException {
    sdkClientInstance();
    NodeManager nodeManager = NodeManager.load(contractAddress, client, keyPair);
    return nodeManager;
  }

  public NodeStorage loadNodeStorageContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException {
    sdkClientInstance();
    NodeStorage nodeStorage = NodeStorage.load(contractAddress, client, keyPair);
    return nodeStorage;
  }

  public FileManager loadFileContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException {
    sdkClientInstance();
    FileManager fileManager = FileManager.load(contractAddress, client, keyPair);
    BigInteger size = fileManager.getSize("");
    logger.debug("FileManager.getSize():{}", size);
    return fileManager;
  }

  public FileStorage loadFileStorageContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException {
    sdkClientInstance();
    FileStorage fileStorage = FileStorage.load(contractAddress, client, keyPair);
    BigInteger size = fileStorage.getSize("");
    logger.debug("File.getSize():{}", size);
    return fileStorage;
  }

  public UserManager loadUserManagerContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException {
    sdkClientInstance();
    UserManager userManager = UserManager.load(contractAddress, client, keyPair);
    return userManager;
  }

  public UserStorage loadUserStorageContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException {
    sdkClientInstance();
    UserStorage userStorage = UserStorage.load(contractAddress, client, keyPair);
    return userStorage;
  }

  public String getTransactionByHash(String hash) {
    sdkClientInstance();
    client.getTransactionReceipt(hash).getTransactionReceipt().get();
    return client.getTransactionByHash(hash).getTransaction().toString();
  }

  public TransactionReceipt getTransactionReceipt(String hash) {
    sdkClientInstance();
    return client.getTransactionReceipt(hash).getTransactionReceipt().get();
  }

  public ChainStorage loadChainStorageContract(CryptoKeyPair keyPair) throws ContractException {
    String address = getContractAddress(CN_ChainStorage);
    return loadChainStorageContract(keyPair, address);
  }

  public Setting loadSettingContract(CryptoKeyPair keyPair) throws ContractException {
    String address = getContractAddress(CN_Setting);
    return loadSettingContract(keyPair, address);
  }

  public NodeManager loadNodeManagerContract(CryptoKeyPair keyPair) throws ContractException {
    String address = getContractAddress(CN_NodeManager);
    return loadNodeManagerContract(keyPair, address);
  }

  public FileManager loadFileContract() throws ContractException {
    String address = getContractAddress(CN_FileManager);
    return loadFileContract(keyPairForRead, address);
  }

  public FileStorage loadFileStorageContract() throws ContractException {
    String address = getContractAddress(CN_FileStorage);
    return loadFileStorageContract(keyPairForRead, address);
  }

  public UserManager loadUserManagerContract() throws ContractException {
    String address = getContractAddress(CN_UserManager);
    return loadUserManagerContract(keyPairForRead, address);
  }

  public UserStorage loadUserStorageContract() throws ContractException {
    String address = getContractAddress(CN_UserStorage);
    return loadUserStorageContract(keyPairForRead, address);
  }

  public NodeManager loadNodeManagerContract() throws ContractException {
    String address = getContractAddress(CN_NodeManager);
    return loadNodeManagerContract(keyPairForRead, address);
  }

  public NodeStorage loadNodeStorageContract() throws ContractException {
    String address = getContractAddress(CN_NodeStorage);
    return loadNodeStorageContract(keyPairForRead, address);
  }
}
