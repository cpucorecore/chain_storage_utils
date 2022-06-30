package com.ancun.chain_storage.controller;

import static com.ancun.chain_storage.constants.Response.CALL_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.Response.SUCCESS;
import static org.fisco.bcos.sdk.utils.ByteUtils.hexStringToBytes;

import com.ancun.chain_storage.config.ContractConfig;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chain_storage/file")
public class FileController {
  Logger logger = LoggerFactory.getLogger(FileController.class);

  @Autowired private ContractConfig contractConfig;

  @GetMapping("exist/{cid}")
  public RespBody<String> handleExist(@PathVariable(value = "cid") String cid)
      throws ContractException {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    Boolean exist = contractConfig.fileStorage().exist(cid);

    resp.setData(exist.toString());
    return resp;
  }

  @GetMapping("get_status/{cid}")
  public RespBody<String> handleGetStatus(@PathVariable(value = "cid") String cid) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    BigInteger status;
    try {
      status = contractConfig.fileStorage().getStatus(cid);
    } catch (ContractException e) {
      logger.warn("file.getSize({}) exception:{}", cid, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(status.toString());
    return resp;
  }

  @GetMapping("get_replica/{cid}")
  public RespBody<String> handleGetReplica(@PathVariable(value = "cid") String cid) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    BigInteger replica;
    try {
      replica = contractConfig.fileStorage().getReplica(cid);
    } catch (ContractException e) {
      logger.warn("fileStorage.getReplica({}) exception:{}", cid, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(replica.toString());
    return resp;
  }

  @GetMapping("get_cid/{cidHash}")
  public RespBody<String> handleGetCid(@PathVariable(value = "cidHash") String cidHash) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    String cid;
    try {
      cid = contractConfig.fileStorage().getCid(hexStringToBytes(cidHash));
    } catch (ContractException e) {
      logger.warn("fileStorage.cid({}) exception:{}", cidHash, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(cid);
    return resp;
  }

  @GetMapping("get_size/{cid}")
  public RespBody<String> handleGetSize(@PathVariable(value = "cid") String cid) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    BigInteger size;
    try {
      size = contractConfig.fileStorage().getSize(cid);
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

    Boolean userExist;
    try {
      userExist = contractConfig.fileStorage().userExist(cid, address);
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

    List<String> users;
    try {
      users = contractConfig.fileStorage().getUsers(cid);
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

    Boolean nodeExist;
    try {
      nodeExist = contractConfig.fileStorage().nodeExist(cid, address);
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

    List<String> nodes;
    try {
      nodes = contractConfig.fileStorage().getNodes(cid);
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

    BigInteger totalSize;
    try {
      totalSize = contractConfig.fileStorage().getTotalSize();
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

    BigInteger fileCount;
    try {
      fileCount = contractConfig.fileStorage().getFileCount();
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
