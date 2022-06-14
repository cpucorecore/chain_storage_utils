package com.ancun.chain_storage.service_blockchain;

import com.ancun.chain_storage.contracts.ChainStorage;
import com.ancun.chain_storage.contracts.FileManager;
import com.ancun.chain_storage.contracts.FileStorage;
import com.ancun.chain_storage.contracts.NodeManager;
import com.ancun.chain_storage.contracts.NodeStorage;
import com.ancun.chain_storage.contracts.Resolver;
import com.ancun.chain_storage.contracts.Setting;
import com.ancun.chain_storage.contracts.UserManager;
import com.ancun.chain_storage.contracts.UserStorage;
import java.util.Map;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

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

  Setting loadSettingContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException;

  NodeManager loadNodeManagerContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException;

  NodeStorage loadNodeStorageContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException;

  FileManager loadFileContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException;

  FileStorage loadFileStorageContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException;

  UserManager loadUserManagerContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException;

  UserStorage loadUserStorageContract(CryptoKeyPair keyPair, String contractAddress)
      throws ContractException;

  ChainStorage loadChainStorageContract(CryptoKeyPair keyPair) throws ContractException;

  Setting loadSettingContract(CryptoKeyPair keyPair) throws ContractException;

  NodeManager loadNodeManagerContract(CryptoKeyPair keyPair) throws ContractException;

  FileManager loadFileContract() throws ContractException;

  FileStorage loadFileStorageContract() throws ContractException;

  UserManager loadUserManagerContract() throws ContractException;

  UserStorage loadUserStorageContract() throws ContractException;

  NodeManager loadNodeManagerContract() throws ContractException;

  NodeStorage loadNodeStorageContract() throws ContractException;
}
