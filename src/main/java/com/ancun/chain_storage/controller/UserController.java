package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.UserStorage;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
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

import static com.ancun.chain_storage.constants.NFTResponseInfo.CONTRACT_EXCEPTION;
import static com.ancun.chain_storage.constants.NFTResponseInfo.SUCCESS;

@RestController
@RequestMapping("chain_storage/user")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private BlockchainService blockchainService;

    @GetMapping("exist/{address}")
    public RespBody<String> handleExist(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        UserStorage userStorage = null;
        try {
            userStorage = blockchainService.loadUserStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Boolean exist = false;
        try {
            exist = userStorage.exist(address);
        } catch (ContractException e) {
            logger.warn("userStorage.exist({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(exist.toString());
        return resp;
    }

    @GetMapping("get_ext/{address}")
    public RespBody<String> handleGetExt(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        UserStorage userStorage = null;
        try {
            userStorage = blockchainService.loadUserStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        String ext = null;
        try {
            ext = userStorage.getExt(address);
        } catch (ContractException e) {
            logger.warn("userStorage.getExt({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(ext);
        return resp;
    }

    @GetMapping("get_storage_used/{address}")
    public RespBody<String> handleGetStorageUsed(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        UserStorage userStorage = null;
        try {
            userStorage = blockchainService.loadUserStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger used = null;
        try {
            used = userStorage.getStorageUsed(address);
        } catch (ContractException e) {
            logger.warn("userStorage.getStorageUsed({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(used.toString());
        return resp;
    }

    @GetMapping("get_storage_total/{address}")
    public RespBody<String> handleGetStorageTotal(@PathVariable(value = "address") String address) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        UserStorage userStorage = null;
        try {
            userStorage = blockchainService.loadUserStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger used = null;
        try {
            used = userStorage.getStorageTotal(address);
        } catch (ContractException e) {
            logger.warn("userStorage.getStorageTotal({}) exception:{}", address, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(used.toString());
        return resp;
    }

    @GetMapping("get_file_ext/{address}/{cid}")
    public RespBody<String> handleGetFileExt(@PathVariable(value = "address") String address, @PathVariable(value = "cid") String cid) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        UserStorage userStorage = null;
        try {
            userStorage = blockchainService.loadUserStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        String ext = null;
        try {
            ext = userStorage.getFileExt(address, cid);
        } catch (ContractException e) {
            logger.warn("userStorage.getFileExt({}, {}) exception:{}", address, cid, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(ext);
        return resp;
    }

    @GetMapping("get_file_duration/{address}/{cid}")
    public RespBody<String> handleGetFileDuration(@PathVariable(value = "address") String address, @PathVariable(value = "cid") String cid) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        UserStorage userStorage = null;
        try {
            userStorage = blockchainService.loadUserStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger duration = null;
        try {
            duration = userStorage.getFileDuration(address, cid);
        } catch (ContractException e) {
            logger.warn("userStorage.getFileDuration({}, {}) exception:{}", address, cid, e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(duration.toString());
        return resp;
    }

    @GetMapping("get_total_user_number")
    public RespBody<String> handleGetTotalUserNumber() {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        UserStorage userStorage = null;
        try {
            userStorage = blockchainService.loadUserStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        BigInteger number;
        try {
            number = userStorage.getTotalUserNumber();
        } catch (ContractException e) {
            logger.warn("userStorage.getTotalUserNumber() exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(number.toString());
        return resp;
    }

    @GetMapping("get_cids/{address}/{page_number}/{page_size}")
    public RespBody<String> handleGetCids(@PathVariable(value = "address") String address,
                                          @PathVariable(value = "page_number") BigInteger pageNumber,
                                          @PathVariable(value = "page_size") BigInteger pageSize) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        UserStorage userStorage = null;
        try {
            userStorage = blockchainService.loadUserStorageContract();
        } catch (ContractException e) {
            logger.warn("loadNodeContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Tuple2<List<String>, Boolean> cids;
        try {
            cids = userStorage.getCids(address, pageNumber, pageSize);
        } catch (ContractException e) {
            logger.warn("userStorage.getFileDuration({}, {}, {}) exception:{}", address, pageNumber.toString(), pageSize.toString(), e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(cids.toString());
        return resp;
    }
}
