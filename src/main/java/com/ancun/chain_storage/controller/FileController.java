package com.ancun.chain_storage.controller;

import static com.ancun.chain_storage.constants.ResponseInfo.CALL_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.ResponseInfo.LOAD_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.ResponseInfo.SUCCESS;

import com.ancun.chain_storage.contracts.FileManager;
import com.ancun.chain_storage.contracts.FileStorage;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import java.math.BigInteger;
import java.util.List;
import javax.annotation.Resource;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chain_storage/file")
public class FileController {
  Logger logger = LoggerFactory.getLogger(FileController.class);

  @Resource private BlockchainService blockchainService;

  @GetMapping("exist/{cid}")
  public RespBody<String> handleExist(@PathVariable(value = "cid") String cid)
      throws ContractException {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    FileStorage fileStorage = blockchainService.loadFileStorageContract();
    Boolean exist = fileStorage.exist(cid);

    resp.setData(exist.toString());
    return resp;
  }

  @GetMapping("get_size/{cid}")
  public RespBody<String> handleGetSize(@PathVariable(value = "cid") String cid) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    FileManager fileManager = null;
    try {
      fileManager = blockchainService.loadFileContract();
    } catch (ContractException e) {
      logger.warn("loadFileContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    BigInteger size;
    try {
      size = fileManager.getSize(cid);
    } catch (ContractException e) {
      logger.warn("file.getSize({}) exception:{}", cid, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(size.toString());
    return resp;
  }

  @GetMapping("user_exist/{cid}/{address}")
  public RespBody<String> handleUserExist(
      @PathVariable(value = "cid") String cid, @PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    FileStorage fileStorage = null;
    try {
      fileStorage = blockchainService.loadFileStorageContract();
    } catch (ContractException e) {
      logger.warn("loadFileContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    Boolean userExist;
    try {
      userExist = fileStorage.userExist(cid, address);
    } catch (ContractException e) {
      logger.warn("fileStorage.userExist({}, {}) exception:{}", cid, address, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(userExist.toString());
    return resp;
  }

  @GetMapping("get_users/{cid}")
  public RespBody<String> handleGetUsers(@PathVariable(value = "cid") String cid) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    FileStorage fileStorage = null;
    try {
      fileStorage = blockchainService.loadFileStorageContract();
    } catch (ContractException e) {
      logger.warn("loadFileContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    List<String> users;
    try {
      users = fileStorage.getUsers(cid);
    } catch (ContractException e) {
      logger.warn("fileStorage.getUsers({}) exception:{}", cid, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(users.toString());
    return resp;
  }

  @GetMapping("node_exist/{cid}/{address}")
  public RespBody<String> handleNodeExist(
      @PathVariable(value = "cid") String cid, @PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    FileStorage fileStorage = null;
    try {
      fileStorage = blockchainService.loadFileStorageContract();
    } catch (ContractException e) {
      logger.warn("loadFileContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    Boolean nodeExist;
    try {
      nodeExist = fileStorage.nodeExist(cid, address);
    } catch (ContractException e) {
      logger.warn("fileStorage.nodeExist({}, {}) exception:{}", cid, address, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(nodeExist.toString());
    return resp;
  }

  @GetMapping("get_nodes/{cid}")
  public RespBody<String> handleGetNodes(@PathVariable(value = "cid") String cid) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    FileStorage fileStorage = null;
    try {
      fileStorage = blockchainService.loadFileStorageContract();
    } catch (ContractException e) {
      logger.warn("loadFileStorageContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    List<String> nodes;
    try {
      nodes = fileStorage.getNodes(cid);
    } catch (ContractException e) {
      logger.warn("fileStorage.getNodes({}) exception:{}", cid, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(nodes.toString());
    return resp;
  }

  @GetMapping("get_total_size")
  public RespBody<String> handleGetTotalSize() {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    FileStorage fileStorage = null;
    try {
      fileStorage = blockchainService.loadFileStorageContract();
    } catch (ContractException e) {
      logger.warn("loadFileContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    BigInteger totalSize;
    try {
      totalSize = fileStorage.getTotalSize();
    } catch (ContractException e) {
      logger.warn("fileStorage.getTotalSize() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(totalSize.toString());
    return resp;
  }

  @GetMapping("get_file_count")
  public RespBody<String> handleGetFileCount() {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    FileStorage fileStorage = null;
    try {
      fileStorage = blockchainService.loadFileStorageContract();
    } catch (ContractException e) {
      logger.warn("loadFileStorageContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    BigInteger fileCount;
    try {
      fileCount = fileStorage.getFileCount();
    } catch (ContractException e) {
      logger.warn("fileStorage.getFileCount() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(fileCount.toString());
    return resp;
  }
}
