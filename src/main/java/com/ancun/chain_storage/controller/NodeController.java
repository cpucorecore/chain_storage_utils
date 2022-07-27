package com.ancun.chain_storage.controller;

import static com.ancun.chain_storage.constants.Response.CALL_CONTRACT_FAILED;
import static com.ancun.chain_storage.constants.Response.SUCCESS;
import static com.ancun.chain_storage.util.CommonUtils.bytesToHexString;

import com.ancun.chain_storage.config.ContractConfig;
import java.math.BigInteger;
import java.util.ArrayList;
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
@RequestMapping("/chain_storage/node")
public class NodeController {
  private Logger logger = LoggerFactory.getLogger(NodeController.class);

  @Autowired private ContractConfig contractConfig;

  @GetMapping("exist/{address}")
  public RespBody<String> handleExist(@PathVariable(value = "address") String address) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    Boolean exist = false;
    try {
      exist = contractConfig.nodeStorage().exist(address);
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

    String ext = null;
    try {
      ext = contractConfig.nodeStorage().getExt(address);
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

    BigInteger used;
    BigInteger total;
    try {
      used = contractConfig.nodeStorage().getStorageUsed(address);
      total = contractConfig.nodeStorage().getStorageTotal(address);
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

    BigInteger nodeCount;
    try {
      nodeCount = contractConfig.nodeStorage().getNodeCount();
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

    Tuple2<List<String>, Boolean> result;
    try {
      result = contractConfig.nodeStorage().getAllNodeAddresses(pageSize, pageNumber);
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

    BigInteger result;
    try {
      result = contractConfig.nodeStorage().getCidCount(nodeAddress);
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

    BigInteger result;
    try {
      result = contractConfig.nodeStorage().getNodeCanAddFileCount(nodeAddress);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getNodeCanAddFileCount() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(result.toString());
    return resp;
  }

  @GetMapping("get_node_can_add_file_cid_hashes/{nodeAddress}")
  public RespBody<List<String>> handleGetNodeCanAddFileCidHashes(
      @PathVariable(value = "nodeAddress") String nodeAddress) {
    RespBody<List<String>> resp = new RespBody<>(SUCCESS);

    List cidHashes;
    try {
      cidHashes = contractConfig.nodeStorage().getNodeCanAddFileCidHashes(nodeAddress);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getNodeCanAddFileCidHashes() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      return resp;
    }

    List<String> cidHashesString = new ArrayList<>(cidHashes.size());
    for (int i = 0; i < cidHashes.size(); i++) {
      cidHashesString.add("0x" + bytesToHexString((byte[]) cidHashes.get(i)));
    }

    resp.setData(cidHashesString);
    return resp;
  }

  @GetMapping("get_can_add_file_node_addresses/{cid}")
  public RespBody<List<String>> handleGetCanAddFileNodeAddresses(
      @PathVariable(value = "cid") String cid) {
    RespBody<List<String>> resp = new RespBody<>(SUCCESS);

    List nodeAddresses;
    try {
      nodeAddresses = contractConfig.nodeStorage().getCanAddFileNodeAddresses(cid);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getCanAddFileNodeAddresses() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      return resp;
    }

    resp.setData(nodeAddresses);
    return resp;
  }

  @GetMapping("get_node_can_delete_file_count/{nodeAddress}")
  public RespBody<String> handleGetNodeCanDeleteFileCount(
      @PathVariable(value = "nodeAddress") String nodeAddress) {
    RespBody<String> resp = new RespBody<>(SUCCESS);

    BigInteger result;
    try {
      result = contractConfig.nodeStorage().getNodeCanDeleteFileCount(nodeAddress);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getNodeCanDeleteFileCount() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      resp.setData(e.getMessage());
      return resp;
    }

    resp.setData(result.toString());
    return resp;
  }

  @GetMapping("get_node_can_delete_file_cid_hashes/{nodeAddress}")
  public RespBody<List<String>> handleGetNodeCanDeleteFileCidHashes(
      @PathVariable(value = "nodeAddress") String nodeAddress) {
    RespBody<List<String>> resp = new RespBody<>(SUCCESS);

    List cidHashes;
    try {
      cidHashes = contractConfig.nodeStorage().getNodeCanDeleteFileCidHashes(nodeAddress);
    } catch (ContractException e) {
      logger.warn("nodeStorage.getNodeCanDeleteFileCidHashes() exception:{}", e.toString());
      resp.setResponseInfo(CALL_CONTRACT_FAILED);
      return resp;
    }

    List<String> cidHashesString = new ArrayList<>(cidHashes.size());
    for (int i = 0; i < cidHashes.size(); i++) {
      cidHashesString.add("0x" + bytesToHexString((byte[]) cidHashes.get(i)));
    }

    resp.setData(cidHashesString);
    return resp;
  }
}
