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
    String getContractAddress(String contractName) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
    Resolver loadResolverContract(CryptoKeyPair keyPair) throws ContractNotExistException, InvalidResolverAddressException;
    Resolver loadResolverContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException, InvalidResolverAddressException;
    ChainStorage loadChainStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    Setting loadSettingContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    Node loadNodeContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    File loadFileContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    User loadUserContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    Task loadTaskContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    Monitor loadMonitorContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    History loadHistoryContract(CryptoKeyPair keyPair, String contractAddress) throws ContractNotExistException;
    ChainStorage loadChainStorageContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
    Setting loadSettingContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
    Node loadNodeContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
    File loadFileContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
    User loadUserContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
    Task loadTaskContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
    Monitor loadMonitorContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
    History loadHistoryContract(CryptoKeyPair keyPair) throws ContractNotExistException, ContractException, InvalidResolverAddressException;
}
