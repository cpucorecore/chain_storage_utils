package com.ancun.chain_storage.service_blockchain.impl;

import com.ancun.chain_storage.config.FileConfig;
import com.ancun.chain_storage.contracts.*;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
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

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlockchainServiceImpl implements BlockchainService {

    public static final String CN_Resolver = "Resolver";
    public static final String CN_Setting = "Setting";
    public static final String CN_ChainStorage = "ChainStorage";
    public static final String CN_Node = "Node";
    public static final String CN_NodeStorage = "NodeStorage";
    public static final String CN_User = "User";
    public static final String CN_UserStorage = "UserStorage";
    public static final String CN_File = "File";
    public static final String CN_FileStorage = "FileStorage";
    public static final String CN_Task = "Task";
    public static final String CN_TaskStorage = "TaskStorage";
    public static final String CN_Monitor = "Monitor";

    public static final String Account_Admin = "Admin";
    public static final byte[] ResolverBytes32 = String2SolidityBytes32(CN_Resolver);
    public static final byte[] SettingBytes32 = String2SolidityBytes32(CN_Setting);
    public static final byte[] ChainStorageBytes32 = String2SolidityBytes32(CN_ChainStorage);
    public static final byte[] NodeBytes32 = String2SolidityBytes32(CN_Node);
    public static final byte[] NodeStorageBytes32 = String2SolidityBytes32(CN_NodeStorage);
    public static final byte[] UserBytes32 = String2SolidityBytes32(CN_User);
    public static final byte[] UserStorageBytes32 = String2SolidityBytes32(CN_UserStorage);
    public static final byte[] FileBytes32 = String2SolidityBytes32(CN_File);
    public static final byte[] FileStorageBytes32 = String2SolidityBytes32(CN_FileStorage);
    public static final byte[] TaskBytes32 = String2SolidityBytes32(CN_Task);
    public static final byte[] TaskStorageBytes32 = String2SolidityBytes32(CN_TaskStorage);
    public static final byte[] MonitorBytes32 = String2SolidityBytes32(CN_Monitor);

    public static final byte[] AdminBytes32 = String2SolidityBytes32(Account_Admin);

    private static final Logger logger = LoggerFactory.getLogger(BlockchainServiceImpl.class);

    @Resource
    private FileConfig fileConfig;

    @Value("${chain.groupId}")
    private int groupId;

    @Value("${resolverAddress}")
    private String resolverAddress;

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
            contractNames.put(CN_ChainStorage, ChainStorageBytes32);
            contractNames.put(CN_Node, NodeBytes32);
            contractNames.put(CN_NodeStorage, NodeStorageBytes32);
            contractNames.put(CN_User, UserBytes32);
            contractNames.put(CN_UserStorage, UserStorageBytes32);
            contractNames.put(CN_File, FileBytes32);
            contractNames.put(CN_FileStorage, FileStorageBytes32);
            contractNames.put(CN_Task, TaskBytes32);
            contractNames.put(CN_TaskStorage, TaskStorageBytes32);
            contractNames.put(CN_Monitor, MonitorBytes32);
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

        for(int i=0; i<valueBytes.length; i++) {
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

        SettingStorage settingStorage = SettingStorage.deploy(client, keyPair, setting.getContractAddress());
        contractAddresses.put("settingStorage", settingStorage.getContractAddress());

        TransactionReceipt receipt = setting.setStorage(settingStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("setting.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(SettingBytes32, setting.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(Setting) failed: " + receipt.getMessage());
        }

        // File and FileStorage
        File file = File.deploy(client, keyPair, resolver.getContractAddress());
        contractAddresses.put("file", file.getContractAddress());
        logger.info("File deploy finish: {}", file.getContractAddress());

        FileStorage fileStorage = FileStorage.deploy(client, keyPair, file.getContractAddress());
        contractAddresses.put("fileStorage", fileStorage.getContractAddress());
        logger.info("FileStorage deploy finish: {}", fileStorage.getContractAddress());

        receipt = file.setStorage(fileStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("file.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(FileBytes32, file.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(File) failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(FileStorageBytes32, fileStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(FileStorage) failed: " + receipt.getMessage());
        }

        // User and UserStorage
        User user = User.deploy(client, keyPair, resolver.getContractAddress());
        contractAddresses.put("user", user.getContractAddress());
        logger.info("User deploy finish: {}", user.getContractAddress());

        UserStorage userStorage = UserStorage.deploy(client, keyPair, user.getContractAddress());
        contractAddresses.put("userStorage", userStorage.getContractAddress());
        logger.info("UserStorage deploy finish: {}", userStorage.getContractAddress());

        receipt = user.setStorage(userStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("user.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(UserBytes32, user.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(User) failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(UserStorageBytes32, userStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(UserStorage) failed: " + receipt.getMessage());
        }

        // Node and NodeStorage
        Node node = Node.deploy(client, keyPair, resolver.getContractAddress());
        contractAddresses.put("node", node.getContractAddress());
        logger.info("Node deploy finish: {}", node.getContractAddress());

        NodeStorage nodeStorage = NodeStorage.deploy(client, keyPair, node.getContractAddress());
        contractAddresses.put("nodeStorage", nodeStorage.getContractAddress());
        logger.info("NodeStorage deploy finish: {}", nodeStorage.getContractAddress());

        receipt = node.setStorage(nodeStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("node.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(NodeBytes32, node.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(Node) failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(NodeStorageBytes32, nodeStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(NodeStorage) failed: " + receipt.getMessage());
        }

        // Task and TaskStorage
        Task task = Task.deploy(client, keyPair, resolver.getContractAddress());
        contractAddresses.put("task", task.getContractAddress());
        logger.info("Task deploy finish: {}", task.getContractAddress());

        TaskStorage taskStorage = TaskStorage.deploy(client, keyPair, task.getContractAddress());
        contractAddresses.put("taskStorage", taskStorage.getContractAddress());
        logger.info("TaskStorage deploy finish: {}", taskStorage.getContractAddress());

        receipt = task.setStorage(taskStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("task.setStorage failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(TaskBytes32, task.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(Task) failed: " + receipt.getMessage());
        }

        receipt = resolver.setAddress(TaskStorageBytes32, taskStorage.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(TaskStorage) failed: " + receipt.getMessage());
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
        receipt = setting.setReplica(BigInteger.valueOf(1));
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

        if(null == resolverAddress || "".equals(resolverAddress)) {
            throw new ContractException("invalid resolver address");
        }

        Resolver resolver = Resolver.load(resolverAddress, client, keyPairForRead);
        String chainStorageAddress = resolver.getAddress(ChainStorageBytes32);
        logger.debug("loadResolverContract() try: Resolver.getAddress(\"ChainStorage\"):{}", chainStorageAddress);
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

    public Setting loadSettingContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        Setting setting = Setting.load(contractAddress, client, keyPair);
        String admin = setting.getAdmin();
        logger.debug("Setting.getAdmin():{}", admin);
        return setting;
    }

    public Node loadNodeContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        Node node = Node.load(contractAddress, client, keyPair);
        return node;
    }

    public NodeStorage loadNodeStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        NodeStorage nodeStorage = NodeStorage.load(contractAddress, client, keyPair);
        return nodeStorage;
    }

    public File loadFileContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        File file = File.load(contractAddress, client, keyPair);
        BigInteger size = file.getSize("");
        logger.debug("File.getSize():{}", size);
        return file;
    }

    public FileStorage loadFileStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        FileStorage fileStorage = FileStorage.load(contractAddress, client, keyPair);
        BigInteger size = fileStorage.getSize("");
        logger.debug("File.getSize():{}", size);
        return fileStorage;
    }

    public User loadUserContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        User user = User.load(contractAddress, client, keyPair);
        return user;
    }

    public UserStorage loadUserStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        UserStorage userStorage = UserStorage.load(contractAddress, client, keyPair);
        return userStorage;
    }

    public Task loadTaskContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        Task task = Task.load(contractAddress, client, keyPair);
        Boolean exist = task.exist(BigInteger.valueOf(1));
        logger.debug("Task.exist():{}", exist);
        return task;
    }

    public TaskStorage loadTaskStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        TaskStorage taskStorage = TaskStorage.load(contractAddress, client, keyPair);
        Boolean exist = taskStorage.exist(BigInteger.valueOf(1));
        logger.debug("Task.exist():{}", exist);
        return taskStorage;
    }

    public Monitor loadMonitorContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException {
        sdkClientInstance();
        Monitor monitor = Monitor.load(contractAddress, client, keyPair);
        return monitor;
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

    public Node loadNodeContract(CryptoKeyPair keyPair) throws ContractException {
        String address = getContractAddress(CN_Node);
        return loadNodeContract(keyPair, address);
    }

    public File loadFileContract() throws ContractException {
        String address = getContractAddress(CN_File);
        return loadFileContract(keyPairForRead, address);
    }

    public FileStorage loadFileStorageContract() throws ContractException {
        String address = getContractAddress(CN_FileStorage);
        return loadFileStorageContract(keyPairForRead, address);
    }

    public User loadUserContract() throws ContractException {
        String address = getContractAddress(CN_User);
        return loadUserContract(keyPairForRead, address);
    }

    public UserStorage loadUserStorageContract() throws ContractException {
        String address = getContractAddress(CN_UserStorage);
        return loadUserStorageContract(keyPairForRead, address);
    }

    public Task loadTaskContract() throws ContractException {
        String address = getContractAddress(CN_Task);
        return loadTaskContract(keyPairForRead, address);
    }

    public TaskStorage loadTaskStorageContract() throws ContractException {
        String address = getContractAddress(CN_TaskStorage);
        return loadTaskStorageContract(keyPairForRead, address);
    }

    public Monitor loadMonitorContract(CryptoKeyPair keyPair) throws ContractException {
        String address = getContractAddress(CN_Monitor);
        return loadMonitorContract(keyPair, address);
    }

    public Node loadNodeContract() throws ContractException {
        String address = getContractAddress(CN_Node);
        return loadNodeContract(keyPairForRead, address);
    }

    public NodeStorage loadNodeStorageContract() throws ContractException {
        String address = getContractAddress(CN_NodeStorage);
        return loadNodeStorageContract(keyPairForRead, address);
    }
}
