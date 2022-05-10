package com.ancun.chain_storage.service_blockchain;

import com.ancun.chain_storage.contracts.*;
import com.ancun.chain_storage.service_blockchain.impl.ContractNotExistException;
import com.ancun.chain_storage.service_blockchain.impl.InvalidResolverAddressException;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

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
    void setResolverAddress(String address);
    String getResolverAddress();
    String getContractAddress(String contractName) throws ContractException;
    Resolver loadResolverContract() throws ContractException;
    Resolver loadResolverContract(String contractAddress) throws ContractException;
    ChainStorage loadChainStorageContract(CryptoKeyPair keyPair, String contractAddress);
    Setting loadSettingContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    Node loadNodeContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    File loadFileContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    User loadUserContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    Task loadTaskContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    Monitor loadMonitorContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    History loadHistoryContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    ChainStorage loadChainStorageContract(CryptoKeyPair keyPair) throws ContractException;
    Setting loadSettingContract(CryptoKeyPair keyPair) throws ContractException;
    Node loadNodeContract(CryptoKeyPair keyPair) throws ContractException;
    File loadFileContract() throws ContractException;
    User loadUserContract(CryptoKeyPair keyPair) throws ContractException;
    Task loadTaskContract() throws ContractException;
    Monitor loadMonitorContract(CryptoKeyPair keyPair) throws ContractException;
    History loadHistoryContract(CryptoKeyPair keyPair) throws ContractException;
    Node loadNodeContract() throws ContractException;
}
