package com.ancun.chain_storage.service_blockchain.impl;

import com.ancun.chain_storage.config.FileConfig;
import com.ancun.chain_storage.contracts.*;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlockchainServiceImpl implements BlockchainService {

    public static final String CN_Resolver = "Resolver";
    public static final String CN_Setting = "Setting";
    public static final String CN_ChainStorage = "ChainStorage";
    public static final String CN_Node = "Node";
    public static final String CN_User = "User";
    public static final String CN_File = "File";
    public static final String CN_Task = "Task";
    public static final String CN_Monitor = "Monitor";
    public static final String CN_History = "History";
    public static final byte[] ResolverBytes32 = String2SolidityBytes32(CN_Resolver);
    public static final byte[] SettingBytes32 = String2SolidityBytes32(CN_Setting);
    public static final byte[] ChainStorageBytes32 = String2SolidityBytes32(CN_ChainStorage);
    public static final byte[] NodeBytes32 = String2SolidityBytes32(CN_Node);
    public static final byte[] UserBytes32 = String2SolidityBytes32(CN_User);
    public static final byte[] FileBytes32 = String2SolidityBytes32(CN_File);
    public static final byte[] TaskBytes32 = String2SolidityBytes32(CN_Task);
    public static final byte[] MonitorBytes32 = String2SolidityBytes32(CN_Monitor);
    public static final byte[] HistoryBytes32 = String2SolidityBytes32(CN_History);

    private static final Logger logger = LoggerFactory.getLogger(BlockchainServiceImpl.class);

    @Autowired
    private FileConfig fileConfig;

    @Value("${chain.groupId}")
    private int groupId;

    @Value("${resolverAddress}")
    private String resolverAddress;

    private Client client;
    private BcosSDK sdk;
    private CryptoKeyPair keyPairForRead;
    private Map<String, byte[]> contractNames = new HashMap<>();

    private void sdkClientInstance() {
        String path = fileConfig.getConfigFile();
        if (null == sdk) {
            sdk = BcosSDK.build(path);
        }
        if (null == client) {
            client = sdk.getClient(groupId);

            keyPairForRead = client.getCryptoSuite().createKeyPair();

            contractNames.put(CN_Resolver, ResolverBytes32);
            contractNames.put(CN_Setting, SettingBytes32);
            contractNames.put(CN_ChainStorage, ChainStorageBytes32);
            contractNames.put(CN_Node, NodeBytes32);
            contractNames.put(CN_User, UserBytes32);
            contractNames.put(CN_File, FileBytes32);
            contractNames.put(CN_Task, TaskBytes32);
            contractNames.put(CN_Monitor, MonitorBytes32);
            contractNames.put(CN_History, HistoryBytes32);
        }
    }

    public String deployNFTContract(
            CryptoKeyPair keyPair,
            String name,
            String symbol,
            boolean issueTransferAllowed,
            BigInteger transferInterval,
            BigInteger maxTransferCount)
            throws Exception {
        sdkClientInstance();

        NFT nft =
                NFT.deploy(
                        client,
                        keyPair,
                        name,
                        symbol,
                        issueTransferAllowed,
                        transferInterval,
                        maxTransferCount);

        return nft.getContractAddress();
    }

    public static byte[] String2SolidityBytes32(String value) {
        byte[] valueBytes = value.getBytes();
        byte[] targetBytes = new byte[32];

        for(int i=0; i<valueBytes.length; i++) {
            targetBytes[i] = valueBytes[i];
        }
        return targetBytes;
    }

    public Map<String, String> deployCSContracts(CryptoKeyPair keyPair) throws Exception {
        sdkClientInstance();

        Map<String, String> contractAddrs = new HashMap<>();
        Resolver resolver = Resolver.deploy(client, keyPair);
        contractAddrs.put("resolver", resolver.getContractAddress());
        logger.info("resolver deploy finish: {}", resolver.getContractAddress());

        History history = History.deploy(client, keyPair, resolver.getContractAddress());
        contractAddrs.put("history", history.getContractAddress());
        logger.info("history deploy finish: {}", history.getContractAddress());


        TransactionReceipt receipt = resolver.setAddress(HistoryBytes32, history.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(History) failed: " + receipt.getMessage());
        }
        logger.info("resolver setAddress(History) finish");

        Setting setting = Setting.deploy(client, keyPair);
        contractAddrs.put("setting", setting.getContractAddress());
        logger.info("setting deploy finish: {}", setting.getContractAddress());

        SettingStorage settingStorage = SettingStorage.deploy(client, keyPair, setting.getContractAddress());
        contractAddrs.put("settingStorage", settingStorage.getContractAddress());

        receipt = setting.setStorage(settingStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("setting.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(SettingBytes32, setting.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(Setting) failed: " + receipt.getMessage());
        }

        // File and FileStorage
        File file = File.deploy(client, keyPair, resolver.getContractAddress());
        contractAddrs.put("file", file.getContractAddress());
        logger.info("File deploy finish: {}", file.getContractAddress());

        FileStorage fileStorage = FileStorage.deploy(client, keyPair, file.getContractAddress());
        contractAddrs.put("fileStorage", fileStorage.getContractAddress());
        logger.info("FileStorage deploy finish: {}", fileStorage.getContractAddress());

        receipt = file.setStorage(fileStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("file.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(FileBytes32, file.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(File) failed: " + receipt.getMessage());
        }

        // User and UserStorage
        User user = User.deploy(client, keyPair, resolver.getContractAddress());
        contractAddrs.put("user", user.getContractAddress());
        logger.info("User deploy finish: {}", user.getContractAddress());

        UserStorage userStorage = UserStorage.deploy(client, keyPair, user.getContractAddress());
        contractAddrs.put("userStorage", userStorage.getContractAddress());
        logger.info("UserStorage deploy finish: {}", userStorage.getContractAddress());

        receipt = user.setStorage(userStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("user.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(UserBytes32, user.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(User) failed: " + receipt.getMessage());
        }

        // Node and NodeStorage
        Node node = Node.deploy(client, keyPair, resolver.getContractAddress());
        contractAddrs.put("node", node.getContractAddress());
        logger.info("Node deploy finish: {}", node.getContractAddress());

        NodeStorage nodeStorage = NodeStorage.deploy(client, keyPair, node.getContractAddress());
        contractAddrs.put("nodeStorage", nodeStorage.getContractAddress());
        logger.info("NodeStorage deploy finish: {}", nodeStorage.getContractAddress());

        receipt = node.setStorage(nodeStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("node.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(NodeBytes32, node.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(Node) failed: " + receipt.getMessage());
        }

        // Task and TaskStorage
        Task task = Task.deploy(client, keyPair, resolver.getContractAddress());
        contractAddrs.put("task", task.getContractAddress());
        logger.info("Task deploy finish: {}", task.getContractAddress());

        TaskStorage taskStorage = TaskStorage.deploy(client, keyPair, task.getContractAddress());
        contractAddrs.put("taskStorage", taskStorage.getContractAddress());
        logger.info("TaskStorage deploy finish: {}", taskStorage.getContractAddress());

        receipt = task.setStorage(taskStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("task.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(TaskBytes32, task.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(Task) failed: " + receipt.getMessage());
        }

        // ChainStorage
        ChainStorage chainStorage = ChainStorage.deploy(client, keyPair);
        contractAddrs.put("chainStorage", chainStorage.getContractAddress());
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
        receipt = file.refreshCache();
        if (!receipt.isStatusOK()) {
            throw new Exception("file refresh cache failed: " + receipt.getMessage());
        }
        logger.info("file refreshCache finish");

        receipt = user.refreshCache();
        if (!receipt.isStatusOK()) {
            throw new Exception("user refresh cache failed: " + receipt.getMessage());
        }
        logger.info("user refreshCache finish");

        receipt = node.refreshCache();
        if (!receipt.isStatusOK()) {
            throw new Exception("node refresh cache failed: " + receipt.getMessage());
        }
        logger.info("node refreshCache finish");

        receipt = task.refreshCache();
        if (!receipt.isStatusOK()) {
            throw new Exception("task refresh cache failed: " + receipt.getMessage());
        }
        logger.info("task refreshCache finish");

        receipt = chainStorage.refreshCache();
        if (!receipt.isStatusOK()) {
            throw new Exception("chainStorage refresh cache failed: " + receipt.getMessage());
        }
        logger.info("chainStorage refreshCache finish");

        // setup Settings
        receipt = setting.setReplica(BigInteger.valueOf(2));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setReplica failed: " + receipt.getMessage());
        }
        receipt = setting.setMaxNodeExtLength(BigInteger.valueOf(1024));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setMaxNodeExtLength failed: " + receipt.getMessage());
        }
        receipt = setting.setMaxUserExtLength(BigInteger.valueOf(1024));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setMaxUserExtLength failed: " + receipt.getMessage());
        }
        receipt = setting.setMaxFileExtLength(BigInteger.valueOf(1024));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setMaxFileExtLength failed: " + receipt.getMessage());
        }
        receipt = setting.setMaxCidLength(BigInteger.valueOf(1024));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setMaxCidLength failed: " + receipt.getMessage());
        }
        receipt = setting.setInitSpace(BigInteger.valueOf(1024*1024*5));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setInitSpace failed: " + receipt.getMessage());
        }
        receipt = setting.setTaskAcceptTimeout(BigInteger.valueOf(1000*3600*24));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setTaskAcceptTimeoutSeconds failed: " + receipt.getMessage());
        }
        receipt = setting.setAddFileTaskTimeout(BigInteger.valueOf(1000*3600*24));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setAddFileTaskTimeoutSeconds failed: " + receipt.getMessage());
        }
        receipt = setting.setDeleteFileTaskTimeout(BigInteger.valueOf(1000*3600*24));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setDeleteFileTaskTimeoutSeconds failed: " + receipt.getMessage());
        }
        receipt = setting.setAddFileProgressTimeout(BigInteger.valueOf(1000*3600*24));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setAddFileProgressTimeoutSeconds failed: " + receipt.getMessage());
        }
        receipt = setting.setMaxAddFileFailedCount(BigInteger.valueOf(6));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setMaxAddFileFailedCount failed: " + receipt.getMessage());
        }
        logger.info("setting setup finish");

        resolverAddress = contractAddrs.get("resolver");

        return contractAddrs;
    }

    public void setResolverAddress(String address) {
        resolverAddress = address;
    }
    public String getResolverAddress() {
        return resolverAddress;
    }

    public String getContractAddress(String contractName) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        Resolver resolver = loadResolverContract(keyPairForRead);
        String address = resolver.getAddress(contractNames.get(contractName));
        logger.debug("resolver.getAddress(\"{}\"):{}", contractName, address);
        return address;
    }

    public Resolver loadResolverContract(CryptoKeyPair keyPair) throws ContractNotExistException, InvalidResolverAddressException {
        sdkClientInstance();
        if(null == resolverAddress || "".equals(resolverAddress)) {
            throw new InvalidResolverAddressException(resolverAddress);
        }

        Resolver resolver = Resolver.load(resolverAddress, client, keyPair);
        try {
            String chainStorageAddress = resolver.getAddress(ChainStorageBytes32);
            logger.debug("Resolver.getAddress(\"ChainStorage\"):{}", chainStorageAddress);
        } catch (ContractException e) {
            throw new ContractNotExistException(resolverAddress);
        }
        return resolver;
    }
    public Resolver loadResolverContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException, InvalidResolverAddressException {
        setResolverAddress(contractAddress);
        return loadResolverContract(keyPair);
    }

    public ChainStorage loadChainStorageContract(CryptoKeyPair keyPair, String contractAddress) {
        sdkClientInstance();
        ChainStorage chainStorage = ChainStorage.load(contractAddress, client, keyPair);
        return chainStorage;
    }

    public Setting loadSettingContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException {
        sdkClientInstance();
        Setting setting = Setting.load(contractAddress, client, keyPair);
        try {
            String admin = setting.getAdmin();
            logger.debug("Setting.getAdmin():{}", admin);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }
        return setting;
    }

    public Node loadNodeContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException {
        sdkClientInstance();
        Node node = Node.load(contractAddress, client, keyPair);
        try {
            Boolean exist = node.exist(contractAddress);
            logger.debug("Node.exist():{}", exist);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }
        return node;
    }

    public File loadFileContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException {
        sdkClientInstance();
        File file = File.load(contractAddress, client, keyPair);
        try {
            Boolean exist = file.exist(contractAddress);
            logger.debug("File.exist():{}", exist);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }
        return file;
    }

    public User loadUserContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException {
        sdkClientInstance();
        User user = User.load(contractAddress, client, keyPair);
        try {
            Boolean exist = user.exist(contractAddress);
            logger.debug("User.exist():{}", exist);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }
        return user;
    }

    public Task loadTaskContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException {
        sdkClientInstance();
        Task task = Task.load(contractAddress, client, keyPair);
        try {
            Boolean exist = task.exist(BigInteger.valueOf(1));
            logger.debug("Task.exist():{}", exist);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }
        return task;
    }

    public Monitor loadMonitorContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException {
        sdkClientInstance();
        Monitor monitor = Monitor.load(contractAddress, client, keyPair);
        try {
            Boolean exist = monitor.exist(contractAddress);
            logger.debug("File.exist():{}", exist);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }
        return monitor;
    }

    public History loadHistoryContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException {
        sdkClientInstance();
        History history = History.load(contractAddress, client, keyPair);
        try {
            BigInteger userHistoryNumber = history.getUserHistoryNumber();
            logger.debug("History.getUserHistoryNumber():{}", userHistoryNumber);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }
        return history;
    }

    public NFT loadNFTContract(CryptoKeyPair keyPair, String contractAddress)
            throws ContractNotExistException {
        sdkClientInstance();

        NFT nft = NFT.load(contractAddress, client, keyPair);
        try {
            // 合约不存在会抛出异常: ContractException{responseOutput=null, errorCode=26}
            String name = nft.name();
            logger.debug("nft.name():{}", name);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }

        return nft;
    }

    public String deployEvidenceContract(CryptoKeyPair keyPair) throws Exception {
        sdkClientInstance();
        Evidence evidence = Evidence.deploy(client, keyPair);
        return evidence.getContractAddress();
    }

    public Evidence loadEvidenceContract(CryptoKeyPair keyPair, String contractAddress)
            throws ContractNotExistException {
        sdkClientInstance();

        Evidence evidence = Evidence.load(contractAddress, client, keyPair);
        try {
            BigInteger count = evidence.getCount(BigInteger.ONE);
            logger.debug("evidence.getCount():{}", count);
        } catch (ContractException e) {
            throw new ContractNotExistException(contractAddress);
        }

        return evidence;
    }

    public String getTransactionByHash(String hash) {
        sdkClientInstance();
        return client.getTransactionByHash(hash).getResult().toString();
    }

    public ChainStorage loadChainStorageContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        String address = getContractAddress(CN_ChainStorage);
        return loadChainStorageContract(keyPair, address);
    }

    public Setting loadSettingContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        String address = getContractAddress(CN_Setting);
        return loadSettingContract(keyPair, address);
    }

    public Node loadNodeContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        String address = getContractAddress(CN_Node);
        return loadNodeContract(keyPair, address);
    }

    public File loadFileContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        String address = getContractAddress(CN_File);
        return loadFileContract(keyPair, address);
    }

    public User loadUserContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        String address = getContractAddress(CN_User);
        return loadUserContract(keyPair, address);
    }

    public Task loadTaskContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        String address = getContractAddress(CN_Task);
        return loadTaskContract(keyPair, address);
    }

    public Monitor loadMonitorContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        String address = getContractAddress(CN_Monitor);
        return loadMonitorContract(keyPair, address);
    }

    public History loadHistoryContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException {
        String address = getContractAddress(CN_History);
        return loadHistoryContract(keyPair, address);
    }
}
