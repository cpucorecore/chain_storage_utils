package com.ancun.chain_storage.controller;

import static com.ancun.chain_storage.constants.ResponseInfo.CALL_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.ResponseInfo.LOAD_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.ResponseInfo.SUCCESS;

import com.ancun.chain_storage.contracts.NodeStorage;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import java.math.BigInteger;
import java.util.List;
import javax.annotation.Resource;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chain_storage/node")
public class NodeController {

  private Logger logger = LoggerFactory.getLogger(NodeController.class);

  @Resource private BlockchainService blockchainService;

  @GetMapping("exist/{address}")
  public RespBody<String> handleExist(@PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    NodeStorage nodeStorage = null;
    try {
      nodeStorage = blockchainService.loadNodeStorageContract();
    } catch (ContractException e) {
      logger.warn("loadNodeContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    Boolean exist = false;
    try {
      exist = nodeStorage.exist(address);
    } catch (ContractException e) {
      logger.warn("nodeStorage.exist({}) exception:{}", address, e.toString());
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
    NodeStorage nodeStorage = null;
    try {
      nodeStorage = blockchainService.loadNodeStorageContract();
    } catch (ContractException e) {
      logger.warn("loadNodeContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    String ext = null;
    try {
      ext = nodeStorage.getExt(address);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getExt({}) exception:{}", address, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(ext);
    return resp;
  }

  @GetMapping("get_storage_space_info/{address}")
  public RespBody<String> handleGetStorageSpaceInfo(
      @PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    NodeStorage nodeStorage = null;
    try {
      nodeStorage = blockchainService.loadNodeStorageContract();
    } catch (ContractException e) {
      logger.warn("loadNodeContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    BigInteger used;
    BigInteger total;
    try {
      used = nodeStorage.getStorageUsed(address);
      total = nodeStorage.getStorageTotal(address);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getStorageSpaceInfo({}) exception:{}", address, e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    Tuple2<BigInteger, BigInteger> result = new Tuple2<>(used, total);
    resp.setData(result.toString());
    return resp;
  }

  @GetMapping("get_node_count")
  public RespBody<String> handleGetNodeCount() {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    NodeStorage nodeStorage = null;
    try {
      nodeStorage = blockchainService.loadNodeStorageContract();
    } catch (ContractException e) {
      logger.warn("loadNodeStorageContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    BigInteger nodeCount;
    try {
      nodeCount = nodeStorage.getNodeCount();
    } catch (ContractException e) {
      logger.warn("nodeStorage.getNodeCount() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(nodeCount.toString());
    return resp;
  }

  @GetMapping("get_all_node_addresses/{pageSize}/{pageNumber}")
  public RespBody<String> handleGetAllNodeAddresses(
      @PathVariable(value = "pageSize") BigInteger pageSize,
      @PathVariable(value = "pageNumber") BigInteger pageNumber) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    NodeStorage nodeStorage = null;
    try {
      nodeStorage = blockchainService.loadNodeStorageContract();
    } catch (ContractException e) {
      logger.warn("loadNodeStorageContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    Tuple2<List<String>, Boolean> result;
    try {
      result = nodeStorage.getAllNodeAddresses(pageSize, pageNumber);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getAllNodeAddresses() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(result.toString());
    return resp;
  }

  @GetMapping("get_cid_count/{nodeAddress}")
  public RespBody<String> handleGetCidCount(
      @PathVariable(value = "nodeAddress") String nodeAddress) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    NodeStorage nodeStorage = null;
    try {
      nodeStorage = blockchainService.loadNodeStorageContract();
    } catch (ContractException e) {
      logger.warn("loadNodeStorageContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    BigInteger result;
    try {
      result = nodeStorage.getCidCount(nodeAddress);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getCidCount() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(result.toString());
    return resp;
  }

  @GetMapping("get_node_can_add_file_count/{nodeAddress}")
  public RespBody<String> handleGetNodeCanAddFileCount(
      @PathVariable(value = "nodeAddress") String nodeAddress) {
    RespBody<String> resp = new RespBody<>(SUCCESS);
    NodeStorage nodeStorage = null;
    try {
      nodeStorage = blockchainService.loadNodeStorageContract();
    } catch (ContractException e) {
      logger.warn("loadNodeStorageContract exception:{}", e.toString());
      resp.setResponseInfo(LOAD_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    BigInteger result;
    try {
      result = nodeStorage.getNodeCanAddFileCount(nodeAddress);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getNodeCanAddFileCount() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(result.toString());
    return resp;
  }
}
