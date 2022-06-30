package com.ancun.chain_storage.controller;

import static com.ancun.chain_storage.constants.Response.CALL_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.Response.SUCCESS;

import com.ancun.chain_storage.config.ContractConfig;
import com.ancun.chain_storage.constants.Response;
import com.ancun.chain_storage.contracts.ChainStorage;
import com.ancun.chain_storage.requests.NodeRegisterRequest;
import com.ancun.chain_storage.requests.UserAddFileRequest;
import java.math.BigInteger;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chain_storage")
public class ChainStorageController {
  private Logger logger = LoggerFactory.getLogger(ChainStorageController.class);

  @Value("${passwd}")
  private String passwd;

  @Autowired private Client client;
  @Autowired private ContractConfig contractConfig;
  @Autowired TransactionDecoderService decoder;

  private CryptoKeyPair loadKeyPair(String address) {
    String filePath = client.getCryptoSuite().getCryptoKeyPair().getP12KeyStoreFilePath(address);
    client.getCryptoSuite().loadAccount("p12", filePath, passwd);
    return client.getCryptoSuite().getCryptoKeyPair();
  }

  @PostMapping("node_register/{nodeAddress}")
  public RespBody<String> handleNodeRegister(
      @PathVariable(value = "nodeAddress") String nodeAddress,
      @RequestBody NodeRegisterRequest request) {

    if (false == request.check()) {
      return new RespBody<>(Response.INVALID_REQUEST);
    }

    CryptoKeyPair keyPair = loadKeyPair(nodeAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.nodeRegister(request.getSpace(), request.getExt());
    if (!receipt.isStatusOK()) {
      String msg =
          "nodeRegister failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @PostMapping("node_set_ext/{nodeAddress}/{ext}")
  public RespBody<String> handleNodeSetExt(
      @PathVariable(value = "nodeAddress") String nodeAddress,
      @PathVariable(value = "ext") String ext) {
    CryptoKeyPair keyPair = loadKeyPair(nodeAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.nodeSetExt(ext);
    if (!receipt.isStatusOK()) {
      String msg =
          "nodeSetExt failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.error(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @PostMapping("node_can_add_file/{nodeAddress}/{cid}/{size}")
  public RespBody<String> handleNodeCanAddFile(
      @PathVariable(value = "nodeAddress") String nodeAddress,
      @PathVariable(value = "cid") String cid,
      @PathVariable(value = "size") BigInteger size) {
    CryptoKeyPair keyPair = loadKeyPair(nodeAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.nodeCanAddFile(cid, size);
    if (!receipt.isStatusOK()) {
      String msg =
          "nodeCanAddFile failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @PostMapping("node_add_file/{nodeAddress}/{cid}")
  public RespBody<String> handleNodeAddFile(
      @PathVariable(value = "nodeAddress") String nodeAddress,
      @PathVariable(value = "cid") String cid) {
    CryptoKeyPair keyPair = loadKeyPair(nodeAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.nodeAddFile(cid);
    if (!receipt.isStatusOK()) {
      String msg =
          "nodeAddFile failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @PostMapping("node_can_delete_file/{nodeAddress}/{cid}")
  public RespBody<String> handleNodeCanDeleteFile(
      @PathVariable(value = "nodeAddress") String nodeAddress,
      @PathVariable(value = "cid") String cid) {
    CryptoKeyPair keyPair = loadKeyPair(nodeAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.nodeCanDeleteFile(cid);
    if (!receipt.isStatusOK()) {
      String msg =
          "nodeCanDeleteFile failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @PostMapping("node_delete_file/{nodeAddress}/{cid}")
  public RespBody<String> handleNodeDeleteFile(
      @PathVariable(value = "nodeAddress") String nodeAddress,
      @PathVariable(value = "cid") String cid) {
    CryptoKeyPair keyPair = loadKeyPair(nodeAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.nodeDeleteFile(cid);
    if (!receipt.isStatusOK()) {
      String msg =
          "nodeDeleteFile failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @PostMapping("node_change_node_space/{nodeAddress}/{space}")
  public RespBody<String> handleNodeChangeNodeSpace(
      @PathVariable(value = "nodeAddress") String nodeAddress,
      @PathVariable(value = "space") BigInteger space) {
    CryptoKeyPair keyPair = loadKeyPair(nodeAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.nodeSetStorageTotal(space);
    if (!receipt.isStatusOK()) {
      String msg =
          "changeNodeSpace failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  // User
  @PostMapping("user_register/{userAddress}/{ext}")
  public RespBody<String> handleUserRegister(
      @PathVariable(value = "userAddress") String userAddress,
      @PathVariable(value = "ext") String ext) {
    CryptoKeyPair keyPair = loadKeyPair(userAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.userRegister(ext);
    if (!receipt.isStatusOK()) {
      String msg =
          "userRegister failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @PostMapping("user_add_file")
  public RespBody<String> handleUserAddFile(
      @PathVariable(value = "userAddress") String userAddress,
      @RequestBody UserAddFileRequest request) {
    CryptoKeyPair keyPair = loadKeyPair(userAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt =
        chainStorage.userAddFile(request.getCid(), request.getDuration(), request.getExt());
    if (!receipt.isStatusOK()) {
      String msg =
          "userAddFile failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @PostMapping("user_delete_file/{userAddress}/{cid}")
  public RespBody<String> handleUserDeleteFile(
      @PathVariable(value = "userAddress") String userAddress,
      @PathVariable(value = "cid") String cid) {
    CryptoKeyPair keyPair = loadKeyPair(userAddress);
    ChainStorage chainStorage = contractConfig.chainStorage(keyPair);

    TransactionReceipt receipt = chainStorage.userDeleteFile(cid);
    if (!receipt.isStatusOK()) {
      String msg =
          "userDeleteFile failed:"
              + getReceiptReturnMessage(receipt)
              + ", txHash:"
              + receipt.getTransactionHash();
      logger.warn(msg);
      return new RespBody<>(CALL_CONTRACT_FAILED, msg);
    }

    return new RespBody<>(SUCCESS, receipt.toString());
  }

  @GetMapping("get_receipt_return_message/{txHash}")
  public RespBody<String> handleParseReceiptMessage(@PathVariable(value = "txHash") String txHash) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    TransactionReceipt receipt = client.getTransactionReceipt(txHash).getTransactionReceipt().get();
    String msg = getReceiptReturnMessage(receipt);
    resp.setData(msg);
    return resp;
  }

  private String getReceiptReturnMessage(TransactionReceipt receipt) {
    TransactionResponse transactionResponse = decoder.decodeReceiptStatus(receipt);
    return transactionResponse.getReturnMessage();
  }
}
