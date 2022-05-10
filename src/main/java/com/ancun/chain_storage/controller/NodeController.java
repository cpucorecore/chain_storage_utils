package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.Node;
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
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Boolean exist = false;
        try {
            exist = node.exist(address);
        } catch (ContractException e) {
            logger.warn("node.exist({}) exception:{}", address, e.toString());
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
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger status;
        try {
            status = node.getStatus(address);
        } catch (ContractException e) {
            logger.warn("node.getStatus({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(status.toString());
        return resp;
    }

    @GetMapping("get_node_cids/{address}")
    public RespBody<String> handleGetNodeCids(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        List<String> cids = null;
        try {
            cids = node.getNodeCids(address);
        } catch (ContractException e) {
            logger.warn("node.getNodeCids({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(cids.toString());
        return resp;
    }

    @GetMapping("get_ext/{address}")
    public RespBody<String> handleGetExt(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        String ext = null;
        try {
            ext = node.getExt(address);
        } catch (ContractException e) {
            logger.warn("node.getExt({}) exception:{}", address, e.toString());
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
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Tuple2<BigInteger, BigInteger> spaceInfo;
        try {
            spaceInfo = node.getStorageSpaceInfo(address);
        } catch (ContractException e) {
            logger.warn("node.getStorageSpaceInfo({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(spaceInfo.toString());
        return resp;
    }

    @GetMapping("get_node_cids_number/{address}")
    public RespBody<String> handleGetNodeCidsNumber(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger number;
        try {
            number = node.getNodeCidsNumber(address);
        } catch (ContractException e) {
            logger.warn("node.getNodeCidsNumber({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(number.toString());
        return resp;
    }

    @GetMapping("get_total_node_number")
    public RespBody<String> handleGetTotalNodeNumber() {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger number;
        try {
            number = node.getTotalNodeNumber();
        } catch (ContractException e) {
            logger.warn("node.getNodeCidsNumber() exception:{}", e.toString());
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
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger number;
        try {
            number = node.getTotalOnlineNodeNumber();
        } catch (ContractException e) {
            logger.warn("node.getTotalOnlineNodeNumber() exception:{}", e.toString());
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
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        List<String> addresses;
        try {
            addresses = node.getAllNodeAddresses();
        } catch (ContractException e) {
            logger.warn("node.getAllNodeAddresses() exception:{}", e.toString());
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
        Node node = null;
        try {
            node = blockchainService.loadNodeContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        List<String> addresses;
        try {
            addresses = node.getAllOnlineNodeAddresses();
        } catch (ContractException e) {
            logger.warn("node.getAllOnlineNodeAddresses() exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(addresses.toString());
        return resp;
    }
}
