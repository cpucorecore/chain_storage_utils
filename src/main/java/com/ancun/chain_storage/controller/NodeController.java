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

import static com.ancun.chain_storage.constants.NFTResponseInfo.*;

@RestController
@RequestMapping("/chain_storage/node")
public class NodeController {

    private Logger logger = LoggerFactory.getLogger(NodeController.class);

    @Resource
    private BlockchainService blockchainService;

    @Resource
    private AccountService accountService;

    @GetMapping("exist/{addr}")
    public RespBody<String> exist(@PathVariable(value = "addr") String addr) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        Node node = null;
        try {
            node = blockchainService.loadRONodeContract();
        } catch (ContractNotExistException e) {
            resp.setNFTResponseInfo(CONTRACT_NOT_EXIST);
            return resp;
        } catch (ContractException e) {
            resp.setNFTResponseInfo(CS_Read_Contract_Exception);
            return resp;
        }
        catch (InvalidResolverAddressException e) {
            resp.setNFTResponseInfo(CS_Invalid_Resolver_Address_Exception);
            return resp;
        }

        Boolean v = false;
        try {
            v = node.exist(addr);
        } catch (ContractException e) {
            resp.setNFTResponseInfo(CS_Read_Contract_Exception);
            return resp;
        }

        resp.setData(v.toString());
        return resp;
    }
}
