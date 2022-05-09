package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.Node;
import com.ancun.chain_storage.contracts.Setting;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_account.AccountService;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import com.ancun.chain_storage.service_blockchain.impl.ContractNotExistException;
import com.ancun.chain_storage.service_blockchain.impl.InvalidResolverAddressException;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("status/{address}")
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
}
