package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.constants.ChainStorageResponseInfo;
import com.ancun.chain_storage.constants.NFTResponseInfo;
import com.ancun.chain_storage.contracts.Setting;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.requests.ChainAccountInfo;
import com.ancun.chain_storage.service_account.AccountService;
import com.ancun.chain_storage.service_account.impl.ChainAccount;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import com.ancun.chain_storage.service_blockchain.impl.ContractNotExistException;
import com.ancun.chain_storage.service_blockchain.impl.InvalidResolverAddressException;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.math.BigInteger;

import static com.ancun.chain_storage.constants.NFTResponseInfo.*;
import static com.ancun.chain_storage.requests.RequestUtils.parseChainAccountInfo;

@RestController
@RequestMapping("/chain_storage/setting")
public class SettingController {
    public static final String Replica = "Replica";
    public static final String InitSpace = "InitSpace";
    public static final String AdminAccount = "AdminAccount";
    public static final String MaxUserExtLength = "MaxUserExtLength";
    public static final String MaxNodeExtLength = "MaxNodeExtLength";
    public static final String MaxMonitorExtLength = "MaxMonitorExtLength";
    public static final String MaxFileExtLength = "MaxFileExtLength";
    public static final String MaxCidLength = "MaxCidLength";
    private Logger logger = LoggerFactory.getLogger(SettingController.class);

    @Resource
    private BlockchainService blockchainService;

    @Resource
    private AccountService accountService;

    @PutMapping("{key}/{value}")
    public RespBody<String> set(@RequestHeader String chainAccountInfo,
                                       @PathVariable(value = "key") String key,
                                       @PathVariable(value = "value") String value) {

        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        Setting setting = null;
        try {
            setting = blockchainService.loadSettingContract(wrap.keyPair);
        } catch (ContractException e) {
            wrap.resp.setNFTResponseInfo(CS_Read_Contract_Exception);
            return wrap.resp;
        }

        TransactionReceipt receipt = null;
        BigInteger bigIntegerValue = BigInteger.ZERO;
        if(!AdminAccount.equals(key)) {
            bigIntegerValue = BigInteger.valueOf(Integer.valueOf(value).longValue()); // TODO check value valid
        }

        switch(key) {
            case Replica:
                receipt = setting.setReplica(bigIntegerValue);
                break;
            case InitSpace:
                receipt = setting.setInitSpace(bigIntegerValue);
                break;
            case AdminAccount:
                receipt = setting.setAdmin(value);
                break;
            case MaxUserExtLength:
                receipt = setting.setMaxUserExtLength(bigIntegerValue);
                break;
            case MaxNodeExtLength:
                receipt = setting.setMaxNodeExtLength(bigIntegerValue);
                break;
            case MaxMonitorExtLength:
                receipt = setting.setMaxMonitorExtLength(bigIntegerValue);
                break;
            case MaxFileExtLength:
                receipt = setting.setMaxFileExtLength(bigIntegerValue);
                break;
            case MaxCidLength:
                receipt = setting.setMaxCidLength(bigIntegerValue);
                break;
            default:
                wrap.resp.setNFTResponseInfo(CS_Setting_Unknown_key);
                logger.error("unknown setting key:{}", key);
                break;
        }

        if(null != receipt) {
            wrap.resp.setData(receipt.toString());
        }

        return wrap.resp;
    }

    @GetMapping("{key}")
    public RespBody<String> get(@RequestHeader String chainAccountInfo, @PathVariable(value = "key") String key) {
        KeyPairWrap wrap = prepareKeyPair(chainAccountInfo);
        if (wrap.resp.getCode() != ChainStorageResponseInfo.SUCCESS.getCode()) {
            logger.error(wrap.resp.toString());
            return wrap.resp;
        }

        Setting setting = null;
        try {
            setting = blockchainService.loadSettingContract(wrap.keyPair);
        } catch (ContractException e) {
            wrap.resp.setNFTResponseInfo(CS_Read_Contract_Exception);
            return wrap.resp;
        }

        BigInteger value = BigInteger.ZERO;
        String admin = "";
        try {
            switch (key) {
                case Replica:
                    value = setting.getReplica();
                    break;
                case InitSpace:
                    value = setting.getInitSpace();
                    break;
                case AdminAccount:
                    admin = setting.getAdmin();
                    break;
                case MaxUserExtLength:
                    value = setting.getMaxUserExtLength();
                    break;
                case MaxNodeExtLength:
                    value = setting.getMaxNodeExtLength();
                    break;
                case MaxMonitorExtLength:
                    value = setting.getMaxMonitorExtLength();
                    break;
                case MaxFileExtLength:
                    value = setting.getMaxFileExtLength();
                    break;
                case MaxCidLength:
                    value = setting.getMaxCidLength();
                    break;
                default:
                    wrap.resp.setNFTResponseInfo(CS_Setting_Unknown_key);
                    logger.error("unknown setting key:{}", key);
                    break;
            }
        } catch (ContractException e) {
            wrap.resp.setNFTResponseInfo(CS_Write_Contract_Exception);
            return wrap.resp;
        }

        if(AdminAccount == key) {
            wrap.resp.setData(admin);
        } else {
            wrap.resp.setData(value.toString());
        }

        return wrap.resp;
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
