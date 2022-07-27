package com.ancun.chain_storage.controller;

import static com.ancun.chain_storage.constants.Response.CALL_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.Response.SUCCESS;

import com.ancun.chain_storage.config.ContractConfig;
import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chain_storage/user")
public class UserController {
  Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired private ContractConfig contractConfig;

  @GetMapping("exist/{address}")
  public RespBody<String> handleExist(@PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    Boolean exist = false;
    try {
      exist = contractConfig.userStorage().exist(address);
    } catch (ContractException e) {
      logger.warn("userStorage.exist({}) exception:{}", address, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(exist.toString());
    return resp;
  }

  @GetMapping("get_ext/{address}")
  public RespBody<String> handleGetExt(@PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    String ext = null;
    try {
      ext = contractConfig.userStorage().getExt(address);
    } catch (ContractException e) {
      logger.warn("userStorage.getExt({}) exception:{}", address, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(ext);
    return resp;
  }

  @GetMapping("get_storage_used/{address}")
  public RespBody<String> handleGetStorageUsed(@PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    BigInteger used = null;
    try {
      used = contractConfig.userStorage().getStorageUsed(address);
    } catch (ContractException e) {
      logger.warn("userStorage.getStorageUsed({}) exception:{}", address, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(used.toString());
    return resp;
  }

  @GetMapping("get_storage_total/{address}")
  public RespBody<String> handleGetStorageTotal(@PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    BigInteger used = null;
    try {
      used = contractConfig.userStorage().getStorageTotal(address);
    } catch (ContractException e) {
      logger.warn("userStorage.getStorageTotal({}) exception:{}", address, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(used.toString());
    return resp;
  }

  @GetMapping("get_file_ext/{address}/{cid}")
  public RespBody<String> handleGetFileExt(
      @PathVariable(value = "address") String address, @PathVariable(value = "cid") String cid) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    String ext = null;
    try {
      ext = contractConfig.userStorage().getFileExt(address, cid);
    } catch (ContractException e) {
      logger.warn("userStorage.getFileExt({}, {}) exception:{}", address, cid, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(ext);
    return resp;
  }

  @GetMapping("get_file_duration/{address}/{cid}")
  public RespBody<String> handleGetFileDuration(
      @PathVariable(value = "address") String address, @PathVariable(value = "cid") String cid) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    BigInteger duration = null;
    try {
      duration = contractConfig.userStorage().getFileDuration(address, cid);
    } catch (ContractException e) {
      logger.warn("userStorage.getFileDuration({}, {}) exception:{}", address, cid, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(duration.toString());
    return resp;
  }

  @GetMapping("get_user_count")
  public RespBody<String> handleGetUserCount() {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    BigInteger number;
    try {
      number = contractConfig.userStorage().getUserCount();
    } catch (ContractException e) {
      logger.warn("userStorage.getUserCount() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(number.toString());
    return resp;
  }

  @GetMapping("get_files/{address}/{page_number}/{page_size}")
  public RespBody<String> handleGetFiles(
      @PathVariable(value = "address") String address,
      @PathVariable(value = "page_number") BigInteger pageNumber,
      @PathVariable(value = "page_size") BigInteger pageSize) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    Tuple2<List<String>, Boolean> files;
    try {
      files = contractConfig.userStorage().getFiles(address, pageSize, pageNumber);
    } catch (ContractException e) {
      logger.warn(
          "userStorage.getFiles({}, {}, {}) exception:{}",
          address,
          pageNumber.toString(),
          pageSize.toString(),
          e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(files.toString());
    return resp;
  }

  @GetMapping("get_user_addresses/{page_number}/{page_size}")
  public RespBody<String> handleGetUserAddresses(
      @PathVariable(value = "page_number") BigInteger pageNumber,
      @PathVariable(value = "page_size") BigInteger pageSize) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    Tuple2<List<String>, Boolean> result;
    try {
      result = contractConfig.userStorage().getAllUserAddresses(pageSize, pageNumber);
    } catch (ContractException e) {
      logger.warn("userStorage.getAllUserAddresses() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(result.toString());
    return resp;
  }
}
