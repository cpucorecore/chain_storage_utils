package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.NodeStorage;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.math.BigInteger;
import java.util.List;

import static com.ancun.chain_storage.constants.NFTResponseInfo.*;

@RestController
@RequestMapping("/chain_storage/node")
public class NodeController {

    private Logger logger = LoggerFactory.getLogger(NodeController.class);

    @Resource
    private BlockchainService blockchainService;

    @GetMapping("exist/{address}")
    public RespBody<String> handleExist(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        NodeStorage nodeStorage = null;
        try {
            nodeStorage = blockchainService.loadNodeStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Boolean exist = false;
        try {
            exist = nodeStorage.exist(address);
        } catch (ContractException e) {
            logger.warn("nodeStorage.exist({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(exist.toString());
        return resp;
    }

    @GetMapping("get_status/{address}")
    public RespBody<String> handleGetStatus(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        NodeStorage nodeStorage = null;
        try {
            nodeStorage = blockchainService.loadNodeStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger status;
        try {
            status = nodeStorage.getStatus(address);
        } catch (ContractException e) {
            logger.warn("nodeStorage.getStatus({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(status.toString());
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
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        String ext = null;
        try {
            ext = nodeStorage.getExt(address);
        } catch (ContractException e) {
            logger.warn("nodeStorage.getExt({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(ext);
        return resp;
    }

    @GetMapping("get_storage_space_info/{address}")
    public RespBody<String> handleGetStorageSpaceInfo(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        NodeStorage nodeStorage = null;
        try {
            nodeStorage = blockchainService.loadNodeStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
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
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Tuple2<BigInteger, BigInteger> result = new Tuple2<>(used, total);
        resp.setData(result.toString());
        return resp;
    }

    @GetMapping("get_total_node_number")
    public RespBody<String> handleGetTotalNodeNumber() {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        NodeStorage nodeStorage = null;
        try {
            nodeStorage = blockchainService.loadNodeStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger number;
        try {
            number = nodeStorage.getTotalNodeNumber();
        } catch (ContractException e) {
            logger.warn("nodeStorage.getTotalNodeNumber() exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(number.toString());
        return resp;
    }

    @GetMapping("get_total_online_node_number")
    public RespBody<String> handleGetTotalOnlineNodeNumber() {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        NodeStorage nodeStorage = null;
        try {
            nodeStorage = blockchainService.loadNodeStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger number;
        try {
            number = nodeStorage.getTotalOnlineNodeNumber();
        } catch (ContractException e) {
            logger.warn("nodeStorage.getTotalOnlineNodeNumber() exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(number.toString());
        return resp;
    }

    @GetMapping("get_all_node_addresses")
    public RespBody<String> handleGetAllNodeAddresses() {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        NodeStorage nodeStorage = null;
        try {
            nodeStorage = blockchainService.loadNodeStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        List<String> addresses;
        try {
            addresses = nodeStorage.getAllNodeAddresses();
        } catch (ContractException e) {
            logger.warn("nodeStorage.getAllNodeAddresses() exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(addresses.toString());
        return resp;
    }

    @GetMapping("get_all_online_node_addresses")
    public RespBody<String> handleGetAllOnlineNodeAddresses() {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        NodeStorage nodeStorage = null;
        try {
            nodeStorage = blockchainService.loadNodeStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        List<String> addresses;
        try {
            addresses = nodeStorage.getAllOnlineNodeAddresses();
        } catch (ContractException e) {
            logger.warn("nodeStorage.getAllOnlineNodeAddresses() exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(addresses.toString());
        return resp;
    }
}
