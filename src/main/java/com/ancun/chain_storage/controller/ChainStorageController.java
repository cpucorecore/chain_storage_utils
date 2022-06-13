package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.constants.ChainStorageResponseInfo;
import com.ancun.chain_storage.constants.NFTResponseInfo;
import com.ancun.chain_storage.contracts.ChainStorage;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.requests.ChainAccountInfo;
import com.ancun.chain_storage.requests.DeployCSContractRequest;
import com.ancun.chain_storage.requests.NodeRegisterRequest;
import com.ancun.chain_storage.requests.UserAddFileRequest;
import com.ancun.chain_storage.service_account.AccountService;
import com.ancun.chain_storage.service_account.impl.ChainAccount;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Map;

import static com.ancun.chain_storage.constants.ChainStorageResponseInfo.CALL_CONTRACT_EXCEPTION;
import static com.ancun.chain_storage.constants.ChainStorageResponseInfo.LOAD_CONTRACT_EXCEPTION;
import static com.ancun.chain_storage.constants.NFTResponseInfo.INVALID_ACCOUNT;
import static com.ancun.chain_storage.requests.RequestUtils.parseChainAccountInfo;

@RestController
@RequestMapping("/chain_storage")
public class ChainStorageController {
    private Logger logger = LoggerFactory.getLogger(ChainStorageController.class);

    @Resource
    private BlockchainService blockchainService;
    @Resource
    private AccountService accountService;

    @PostMapping("/deploy_contract")
    public RespBody<String> deployContract(
            @RequestHeader String chainAccountInfo, @RequestBody DeployCSContractRequest request) {
        logger.debug("request:{}", request.toJsonString());

        if (false == request.check()) {
            return new RespBody<>(ChainStorageResponseInfo.INVALID_REQUEST, request.toJsonString());
        }

        KeyPairWrap warp = prepareKeyPair(chainAccountInfo);
        if (warp.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(warp.resp.toString());
            return warp.resp;
        }

        Map<String, String> result;
        try {
            result = blockchainService.deployCSContracts(warp.keyPair);
            warp.resp.setData(result.toString());
        } catch (Exception e) {
            warp.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_DEPLOY_ERR);
            warp.resp.setData(e.getMessage());
        }
        return warp.resp;
    }

    @PutMapping("resolver/{address}")
    public RespBody<String> setResolver(@PathVariable(value = "address") String address) {
        RespBody<String> response = new RespBody<>(NFTResponseInfo.SUCCESS);
        blockchainService.setResolverAddress(address);
        return response;
    }

    @GetMapping("resolver")
    public RespBody<String> getResolver() {
        RespBody<String> response = new RespBody<>(NFTResponseInfo.SUCCESS);
        response.setData(blockchainService.getResolverAddress());
        return response;
    }

    @PostMapping("node_register")
    public RespBody<String> handleNodeRegister(@RequestHeader String chainAccountInfo, @RequestBody NodeRegisterRequest request) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        if (false == request.check()) {
            wrap.resp.setResponseInfo(ChainStorageResponseInfo.INVALID_REQUEST);
            return wrap.resp;
        }

        ChainStorage chainStorage;
        try {
            chainStorage = blockchainService.loadChainStorageContract(wrap.keyPair);
        } catch (ContractException e) {
            String msg = "loadChainStorageContract Exception:" + e.getMessage();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(LOAD_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        TransactionReceipt receipt = chainStorage.nodeRegister(request.getSpace(), request.getExt());
        if (!receipt.isStatusOK()) {
            String msg = "nodeRegister failed:" + getReceiptReturnMessage(receipt) + ", txHash:" + receipt.getTransactionHash();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(CALL_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        wrap.resp.setData(receipt.toString());
        return wrap.resp;
    }

    @PostMapping("node_set_ext/{ext}")
    public RespBody<String> handleNodeSetExt(@RequestHeader String chainAccountInfo, @PathVariable(value = "ext") String ext) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        ChainStorage chainStorage;
        try {
            chainStorage = blockchainService.loadChainStorageContract(wrap.keyPair);
        } catch (ContractException e) {
            String msg = "loadChainStorageContract Exception:" + e.getMessage();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(LOAD_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        TransactionReceipt receipt = chainStorage.nodeSetExt(ext);
        if (!receipt.isStatusOK()) {
            String msg = "nodeSetExt failed:" + getReceiptReturnMessage(receipt) + ", txHash:" + receipt.getTransactionHash();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(CALL_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        wrap.resp.setData(receipt.toString());
        return wrap.resp;
    }

    @PostMapping("node_can_add_file/{cid}/{size}")
    public RespBody<String> handleNodeCanAddFile(@RequestHeader String chainAccountInfo, @PathVariable(value = "cid") String cid, @PathVariable(value = "size") BigInteger size) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        ChainStorage chainStorage;
        try {
            chainStorage = blockchainService.loadChainStorageContract(wrap.keyPair);
        } catch (ContractException e) {
            String msg = "loadChainStorageContract Exception:" + e.getMessage();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(LOAD_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        TransactionReceipt receipt = chainStorage.nodeCanAddFile(cid, size);
        if (!receipt.isStatusOK()) {
            String msg = "nodeCanAddFile failed:" + getReceiptReturnMessage(receipt) + ", txHash:" + receipt.getTransactionHash();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(CALL_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        wrap.resp.setData(receipt.toString());
        return wrap.resp;
    }

    @PostMapping("node_add_file/{cid}")
    public RespBody<String> handleNodeAddFile(
            @RequestHeader String chainAccountInfo,
            @PathVariable(value = "cid") String cid) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        ChainStorage chainStorage;
        try {
            chainStorage = blockchainService.loadChainStorageContract(wrap.keyPair);
        } catch (ContractException e) {
            String msg = "loadChainStorageContract Exception:" + e.getMessage();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(LOAD_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        TransactionReceipt receipt = chainStorage.nodeAddFile(cid);
        if (!receipt.isStatusOK()) {
            String msg = "nodeAddFile failed:" + getReceiptReturnMessage(receipt) + ", txHash:" + receipt.getTransactionHash();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(CALL_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        wrap.resp.setData(receipt.toString());
        return wrap.resp;
    }

    @PostMapping("node_change_node_space/{space}")
    public RespBody<String> handleNodeChangeNodeSpace(@RequestHeader String chainAccountInfo, @PathVariable(value = "space") BigInteger space) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        ChainStorage chainStorage;
        try {
            chainStorage = blockchainService.loadChainStorageContract(wrap.keyPair);
        } catch (ContractException e) {
            String msg = "loadChainStorageContract Exception:" + e.getMessage();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(LOAD_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        TransactionReceipt receipt = chainStorage.nodeSetStorageTotal(space);
        if (!receipt.isStatusOK()) {
            String msg = "changeNodeSpace failed:" + getReceiptReturnMessage(receipt) + ", txHash:" + receipt.getTransactionHash();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(CALL_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        wrap.resp.setData(receipt.toString());
        return wrap.resp;
    }

    // User
    @PostMapping("user_register/{ext}")
    public RespBody<String> handleUserRegister(@RequestHeader String chainAccountInfo, @PathVariable(value = "ext") String ext) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        ChainStorage chainStorage;
        try {
            chainStorage = blockchainService.loadChainStorageContract(wrap.keyPair);
        } catch (ContractException e) {
            String msg = "loadChainStorageContract Exception:" + e.getMessage();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(LOAD_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        TransactionReceipt receipt = chainStorage.userRegister(ext);
        if (!receipt.isStatusOK()) {
            String msg = "userRegister failed:" + getReceiptReturnMessage(receipt) + ", txHash:" + receipt.getTransactionHash();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(CALL_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        wrap.resp.setData(receipt.toString());
        return wrap.resp;
    }

    @PostMapping("user_add_file")
    public RespBody<String> handleUserAddFile(@RequestHeader String chainAccountInfo, @RequestBody UserAddFileRequest request) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        ChainStorage chainStorage;
        try {
            chainStorage = blockchainService.loadChainStorageContract(wrap.keyPair);
        } catch (ContractException e) {
            String msg = "loadChainStorageContract Exception:" + e.getMessage();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(LOAD_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        TransactionReceipt receipt = chainStorage.userAddFile(request.getCid(), request.getDuration(), request.getExt());
        if (!receipt.isStatusOK()) {
            String msg = "userAddFile failed:" + getReceiptReturnMessage(receipt) + ", txHash:" + receipt.getTransactionHash();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(CALL_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        wrap.resp.setData(receipt.toString());
        return wrap.resp;
    }

    @PostMapping("user_delete_file/{cid}")
    public RespBody<String> handleUserDeleteFile(@RequestHeader String chainAccountInfo, @PathVariable(value = "cid") String cid) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        ChainStorage chainStorage;
        try {
            chainStorage = blockchainService.loadChainStorageContract(wrap.keyPair);
        } catch (ContractException e) {
            String msg = "loadChainStorageContract Exception:" + e.getMessage();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(LOAD_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        TransactionReceipt receipt = chainStorage.userDeleteFile(cid);
        if (!receipt.isStatusOK()) {
            String msg = "userDeleteFile failed:" + getReceiptReturnMessage(receipt) + ", txHash:" + receipt.getTransactionHash();
            logger.warn(msg);
            wrap.resp.setData(msg);
            wrap.resp.setResponseInfo(CALL_CONTRACT_EXCEPTION);
            return wrap.resp;
        }

        wrap.resp.setData(receipt.toString());
        return wrap.resp;
    }

    @GetMapping("get_receipt_return_message/{txHash}")
    public RespBody<String> handleParseReceiptMessage(@PathVariable(value = "txHash") String txHash) {
        RespBody<String> resp = new RespBody<>(NFTResponseInfo.SUCCESS);
        String msg = getReceiptReturnMessage(blockchainService.getTransactionReceipt(txHash));
        resp.setData(msg);
        return resp;
    }

    private KeyPairWrap prepareKeyPair(String chainAccountInfo) {
        RespBody<String> resp = new RespBody<>(NFTResponseInfo.SUCCESS);
        KeyPairWrap keyPairWrap = new KeyPairWrap(null, resp);

        ChainAccountInfo accountInfo;
        try {
            accountInfo = parseChainAccountInfo(chainAccountInfo);
        } catch (Exception e) {
            resp.setNFTResponseInfo(INVALID_ACCOUNT);
            resp.setData(e.getMessage());
            logger.error(resp.toString());
            return keyPairWrap;
        }

        String address = accountInfo.getAddress();
        String password = accountInfo.getPassword();

        ChainAccount chainAccount;
        try {
            chainAccount = accountService.getChainAccount(address, password);
        } catch (Exception e) {
            resp.setNFTResponseInfo(NFTResponseInfo.GET_CHAIN_ACCOUNT_ERR);
            resp.setData(e.getMessage());
            logger.error(resp.toString());
            return keyPairWrap;
        }

        keyPairWrap.keyPair = chainAccount.getCryptoKeyPair();
        return keyPairWrap;
    }

    private String getReceiptReturnMessage(TransactionReceipt receipt) {
        TransactionResponse transactionResponse = blockchainService.getDecoder().decodeReceiptStatus(receipt);
        return transactionResponse.getReturnMessage();
    }
}
