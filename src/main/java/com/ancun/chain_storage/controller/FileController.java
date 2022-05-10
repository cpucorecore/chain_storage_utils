package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.File;
import com.ancun.chain_storage.contracts.Node;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;

import static com.ancun.chain_storage.constants.NFTResponseInfo.CONTRACT_EXCEPTION;
import static com.ancun.chain_storage.constants.NFTResponseInfo.SUCCESS;

@RestController
@RequestMapping("/chain_storage/file")
public class FileController {
    Logger logger = LoggerFactory.getLogger(FileController.class);

    @Resource
    private BlockchainService blockchainService;

    @GetMapping("exist/{cid}")
    public RespBody<String> handleExist(@PathVariable(value = "cid") String cid) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        File file = null;
        try {
            file = blockchainService.loadFileContract();
        } catch (ContractException e) {
            logger.warn("loadFileContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Boolean exist;
        try {
            exist = file.exist(cid);
        } catch (ContractException e) {
            logger.warn("file.exist({}) exception:{}", cid, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(exist.toString());
        return resp;
    }

    @GetMapping("getSize/{cid}")
    public RespBody<String> handleGetSize(@PathVariable(value = "cid") String cid) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        File file = null;
        try {
            file = blockchainService.loadFileContract();
        } catch (ContractException e) {
            logger.warn("loadFileContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger size;
        try {
            size = file.getSize(cid);
        } catch (ContractException e) {
            logger.warn("file.getSize({}) exception:{}", cid, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(size.toString());
        return resp;
    }

    @GetMapping("owner_exist/{cid}/{address}")
    public RespBody<String> handleOwnerExist(@PathVariable(value = "cid") String cid, @PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        File file = null;
        try {
            file = blockchainService.loadFileContract();
        } catch (ContractException e) {
            logger.warn("loadFileContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Boolean ownerExist;
        try {
            ownerExist = file.ownerExist(cid, address);
        } catch (ContractException e) {
            logger.warn("file.ownerExist({}, {}) exception:{}", cid, address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(ownerExist.toString());
        return resp;
    }
}
