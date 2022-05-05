package com.ancun.chain_storage.service_blockchain.impl;

import com.ancun.chain_storage.config.FileConfig;
import com.ancun.chain_storage.constants.NFTResponseInfo;
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
import java.util.Set;

@Service
public class BlockchainServiceImpl implements BlockchainService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainServiceImpl.class);

    @Autowired
    private FileConfig fileConfig;

    @Value("${chain.groupId}")
    private int groupId;

    private Client client;
    private BcosSDK sdk;

    private void sdkClientInstance() {
        String path = fileConfig.getConfigFile();
        if (null == sdk) {
            sdk = BcosSDK.build(path);
        }
        if (null == client) {
            client = sdk.getClient(groupId);
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

    private byte[] String2SolidityBytes32(String value) {
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


        TransactionReceipt receipt = resolver.setAddress(String2SolidityBytes32("History"), history.getContractAddress());
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

        receipt = resolver.setAddress(String2SolidityBytes32("Setting"), setting.getContractAddress());
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

        receipt = resolver.setAddress(String2SolidityBytes32("File"), file.getContractAddress());
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

        receipt = resolver.setAddress(String2SolidityBytes32("User"), user.getContractAddress());
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

        receipt = resolver.setAddress(String2SolidityBytes32("Node"), node.getContractAddress());
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

        receipt = resolver.setAddress(String2SolidityBytes32("Task"), task.getContractAddress());
        if (!receipt.isStatusOK()) {
            throw new Exception("resolver.setAddress(Task) failed: " + receipt.getMessage());
        }

        // ChainStorage
        ChainStorage chainStorage = ChainStorage.deploy(client, keyPair);
        contractAddrs.put("chainStorage", chainStorage.getContractAddress());
        receipt = resolver.setAddress(String2SolidityBytes32("ChainStorage"), chainStorage.getContractAddress());
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
        receipt = setting.setTaskAcceptTimeoutSeconds(BigInteger.valueOf(1000*3600*24));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setTaskAcceptTimeoutSeconds failed: " + receipt.getMessage());
        }
        receipt = setting.setAddFileTaskTimeoutSeconds(BigInteger.valueOf(1000*3600*24));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setAddFileTaskTimeoutSeconds failed: " + receipt.getMessage());
        }
        receipt = setting.setDeleteFileTaskTimeoutSeconds(BigInteger.valueOf(1000*3600*24));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setDeleteFileTaskTimeoutSeconds failed: " + receipt.getMessage());
        }
        receipt = setting.setAddFileProgressTimeoutSeconds(BigInteger.valueOf(1000*3600*24));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setAddFileProgressTimeoutSeconds failed: " + receipt.getMessage());
        }
        receipt = setting.setMaxAddFileFailedCount(BigInteger.valueOf(6));
        if (!receipt.isStatusOK()) {
            throw new Exception("setting setMaxAddFileFailedCount failed: " + receipt.getMessage());
        }
        logger.info("setting setup finish");

        return contractAddrs;
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
            // 合约不存在会抛出异常: ContractException{responseOutput=null, errorCode=26}
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
}
