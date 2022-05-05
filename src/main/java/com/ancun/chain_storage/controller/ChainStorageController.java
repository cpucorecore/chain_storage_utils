package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.constants.ChainStorageResponseInfo;
import com.ancun.chain_storage.constants.NFTResponseInfo;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.requests.ChainAccountInfo;
import com.ancun.chain_storage.requests.DeployCSContractRequest;
import com.ancun.chain_storage.service_account.AccountService;
import com.ancun.chain_storage.service_account.impl.ChainAccount;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Map;

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

    private KeyPairWrap prepareKeyPair(String chainAccountInfo) {
        RespBody<String> resp = new RespBody<>(NFTResponseInfo.SUCCESS);
        KeyPairWrap keyPairWrap = new KeyPairWrap(null, resp);

        ChainAccountInfo accountInfo;
        try {
            accountInfo = parseChainAccountInfo(chainAccountInfo);
        } catch (Exception e) {
            resp.setNFTResponseInfo(NFTResponseInfo.INVALID_ACCOUNT);
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
}
