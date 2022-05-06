package com.ancun.chain_storage.service_blockchain;

import com.ancun.chain_storage.contracts.*;
import com.ancun.chain_storage.service_blockchain.impl.ContractNotExistException;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;

import java.math.BigInteger;
import java.util.Map;

public interface BlockchainService {
    String deployNFTContract(
            CryptoKeyPair keyPair,
            String name,
            String symbol,
            boolean issueTransferAllowed,
            BigInteger transferInterval,
            BigInteger maxTransferCount)
            throws Exception;

    NFT loadNFTContract(CryptoKeyPair keyPair, String contractAddress)
            throws ContractNotExistException;

    String deployEvidenceContract(CryptoKeyPair keyPair) throws Exception;

    Evidence loadEvidenceContract(CryptoKeyPair keyPair, String contractAddress)
            throws ContractNotExistException;

    String getTransactionByHash(String hash);

    Map<String, String> deployCSContracts(CryptoKeyPair keyPair) throws Exception;
    ChainStorage loadChainStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    Setting loadSettingContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    Node loadNodeContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    File loadFileContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    User loadUserContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    Task loadTaskContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    Monitor loadMonitorContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    History loadHistoryContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
}
