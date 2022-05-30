package com.ancun.chain_storage.service_blockchain;

import com.ancun.chain_storage.contracts.*;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

import java.math.BigInteger;
import java.util.Map;

public interface BlockchainService {
    TransactionDecoderInterface getDecoder();
    String getTransactionByHash(String hash);
    TransactionReceipt getTransactionReceipt(String hash);

    Map<String, String> deployCSContracts(CryptoKeyPair keyPair) throws Exception;
    void setResolverAddress(String address);
    String getResolverAddress();
    String getContractAddress(String contractName) throws ContractException;
    Resolver loadResolverContract() throws ContractException;
    Resolver loadResolverContract(String contractAddress) throws ContractException;
    ChainStorage loadChainStorageContract(CryptoKeyPair keyPair, String contractAddress);
    Setting loadSettingContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    Node loadNodeContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    NodeStorage loadNodeStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    File loadFileContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    FileStorage loadFileStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    User loadUserContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    UserStorage loadUserStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    Task loadTaskContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    TaskStorage loadTaskStorageContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    Monitor loadMonitorContract(CryptoKeyPair keyPair, String contractAddress) throws ContractException;
    ChainStorage loadChainStorageContract(CryptoKeyPair keyPair) throws ContractException;
    Setting loadSettingContract(CryptoKeyPair keyPair) throws ContractException;
    Node loadNodeContract(CryptoKeyPair keyPair) throws ContractException;
    File loadFileContract() throws ContractException;
    FileStorage loadFileStorageContract() throws ContractException;
    User loadUserContract() throws ContractException;
    UserStorage loadUserStorageContract() throws ContractException;
    Task loadTaskContract() throws ContractException;
    TaskStorage loadTaskStorageContract() throws ContractException;
    Monitor loadMonitorContract(CryptoKeyPair keyPair) throws ContractException;
    Node loadNodeContract() throws ContractException;
    NodeStorage loadNodeStorageContract() throws ContractException;
}
