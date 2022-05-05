package com.ancun.chain_storage.controller;

import com.alibaba.fastjson.JSON;
import com.ancun.chain_storage.constants.ChainStorageResponseInfo;
import com.ancun.chain_storage.contracts.Evidence;
import com.ancun.chain_storage.requests.*;
import com.ancun.chain_storage.service_account.impl.ChainAccount;
import com.ancun.chain_storage.service_blockchain.impl.ContractNotExistException;
import com.ancun.chain_storage.constants.NFTResponseInfo;
import com.ancun.chain_storage.contracts.NFT;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_account.AccountService;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import com.ancun.chain_storage.service_metadata.MetadataService;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ancun.chain_storage.requests.RequestUtils.*;

@RestController
@RequestMapping("/nft")
public class NFTController {

    private Logger logger = LoggerFactory.getLogger(NFTController.class);

    @Resource
    private BlockchainService blockchainService;
    @Resource
    private AccountService accountService;
    @Resource
    private MetadataService metadataService;

    private Map<String, NFT> cacheForNFTQuery = new HashMap<>();

    private TransferEventCallback transferEventCallback = new TransferEventCallback();

    private Map<String, Evidence> cacheForEvidenceQuery = new HashMap<>();

    private TransferCallback transferCallback = new TransferCallback();

    // GET api for query

    @GetMapping("/get_transaction/{tx_id}")
    public RespBody<String> getTransaction(@PathVariable(value = "tx_id") String txId) {
        RespBody<String> resp = new RespBody<>(NFTResponseInfo.SUCCESS);
        String transactionData = blockchainService.getTransactionByHash(txId);
        resp.setData(transactionData);
        return resp;
    }

    @GetMapping("/receipt/{contract}/get_tx_ids/{token_id}")
    public RespBody<String> getTxIds(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {
        return getTxIdsHandler(new TokenIdCommonRequest(contract, tokenId));
    }

    @GetMapping("/receipt/{contract}/get_exts/{token_id}")
    public RespBody<String> getExts(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {
        return getExtsHandler(new TokenIdCommonRequest(contract, tokenId));
    }

    @GetMapping("/receipt/{contract}/get_count/{token_id}")
    public RespBody<String> getCount(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {
        return getCountHandler(new TokenIdCommonRequest(contract, tokenId));
    }

    @GetMapping("/{contract}/balance_of/{owner}")
    public RespBody<String> balanceOf(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "owner") String owner) {
        return balanceOfHandler(new BalanceOfRequest(contract, owner));
    }

    @GetMapping("/{contract}/name")
    public RespBody<String> name(@PathVariable(value = "contract") String contract) {
        return nameHandler(new CommonRequest(contract));
    }

    @GetMapping("/{contract}/symbol")
    public RespBody<String> symbol(@PathVariable(value = "contract") String contract) {
        return symbolHandler(new CommonRequest(contract));
    }

    @GetMapping("/{contract}/token_uri/{token_id}")
    public RespBody<String> baseURI(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {
        return tokenURIHandler(new TokenIdCommonRequest(contract, tokenId));
    }

    @GetMapping("/{contract}/base_uri")
    public RespBody<String> baseURI(@PathVariable(value = "contract") String contract) {
        return baseURIHandler(new CommonRequest(contract));
    }

    @GetMapping("/{contract}/owner_of/{token_id}")
    public RespBody<String> ownerOf(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {
        return ownerOfHandler(new TokenIdCommonRequest(contract, tokenId));
    }

    @GetMapping("/{contract}/token_of_owner_by_index/{owner}/{index}")
    public RespBody<String> tokenOfOwnerByIndex(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "owner") String owner,
            @PathVariable(value = "index") BigInteger index) {
        return tokenOfOwnerByIndexHandler(new TokenOfOwnerByIndexRequest(contract, owner, index));
    }

    @GetMapping("/{contract}/total_supply")
    public RespBody<String> totalSupply(@PathVariable(value = "contract") String contract) {
        return totalSupplyHandler(new CommonRequest(contract));
    }

    @GetMapping("/{contract}/token_by_index/{index}")
    public RespBody<String> tokenByIndex(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "index") BigInteger index) {
        return tokenByIndexHandler(new TokenByIndexRequest(contract, index));
    }

    @GetMapping("/{contract}/get_approved/{token_id}")
    public RespBody<String> get_approved(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {
        return getApprovedHandler(new TokenIdCommonRequest(contract, tokenId));
    }

    @GetMapping("/{contract}/is_approved_for_all/{owner}/{operator}")
    public RespBody<String> isApprovedForAll(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "owner") String owner,
            @PathVariable(value = "operator") String operator) {
        return isApprovedForAllHandler(new IsApprovedForAllRequest(contract, owner, operator));
    }

    // TODO: POST to GET
    @PostMapping("/access_chain_account")
    public RespBody<String> accessChainAccount(@RequestHeader String chainAccountInfo) {
        KeyPairWrap warp = prepareKeyPair(chainAccountInfo);
        return warp.resp;
    }

    @GetMapping("/{contract}/issue_transfer_allowed")
    public RespBody<String> issueTransferAllowed(
            @PathVariable(value = "contract") String contract) {
        return issueTransferAllowedHandler(new CommonRequest(contract));
    }

    @GetMapping("/{contract}/transfer_interval")
    public RespBody<String> transferInterval(@PathVariable(value = "contract") String contract) {
        return transferIntervalHandler(new CommonRequest(contract));
    }

    @GetMapping("/{contract}/max_transfer_count")
    public RespBody<String> maxTransferCount(@PathVariable(value = "contract") String contract) {
        return maxTransferCountHandler(new CommonRequest(contract));
    }

    @GetMapping("/{contract}/transfer_count/{token_id}")
    public RespBody<String> transferCount(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {
        return transferCountHandler(new TokenIdCommonRequest(contract, tokenId));
    }

    @GetMapping("/{contract}/last_transfer_timestamp/{token_id}")
    public RespBody<String> lastTransferTimestamp(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {
        return lastTransferTimestampHandler(new TokenIdCommonRequest(contract, tokenId));
    }

    // POST api for modify
    @PostMapping("/create_chain_account")
    public RespBody<String> createChainAccount(@RequestBody CreateChainAccountRequest request) {
        if (!checkPassword(request.getPassword())) {
            logger.warn("invalid password");
            return new RespBody<>(NFTResponseInfo.INVALID_PASSWORD);
        }

        try {
            ChainAccount chainAccount = accountService.createChainAccount(request.getPassword());
            String address = chainAccount.getCryptoKeyPair().getAddress();
            logger.debug("create_chain_account success, new address:{}", address);
            return new RespBody<>(NFTResponseInfo.SUCCESS, address);
        } catch (IOException e) {
            logger.debug("create_chain_account failed:{}", e.getMessage());
            return new RespBody<>(NFTResponseInfo.CREATE_CHAIN_ACCOUNT_ERR, e.getMessage());
        }
    }

    @PostMapping("/deploy_contract")
    public RespBody<String> deployContract(
            @RequestHeader String chainAccountInfo, @RequestBody DeployNFTContractRequest request) {
        logger.debug("request:{}", request.toJsonString());

        if (false == request.check()) {
            return new RespBody<>(NFTResponseInfo.INVALID_REQUEST, request.toJsonString());
        }

        KeyPairWrap warp = prepareKeyPair(chainAccountInfo);
        if (warp.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            logger.error(warp.resp.toString());
            return warp.resp;
        }

        String nftName = request.getName();
        String nftSymbol = request.getSymbol();
        boolean issueTransferAllowed = request.isIssueTransferAllowed();
        BigInteger maxTransferCount = request.getMaxTransferCount();
        BigInteger transferInterval = request.getTransferInterval();
        String result;
        try {
            result =
                    blockchainService.deployNFTContract(
                            warp.keyPair,
                            nftName,
                            nftSymbol,
                            issueTransferAllowed,
                            maxTransferCount,
                            transferInterval);
            warp.resp.setData(result);
        } catch (Exception e) {
            warp.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_DEPLOY_ERR);
            warp.resp.setData(e.getMessage());
        }
        return warp.resp;
    }

    @PutMapping("/receipt/deploy_contract")
    public RespBody<String> deployReceiptContract(@RequestHeader String chainAccountInfo) {
        KeyPairWrap warp = prepareKeyPair(chainAccountInfo);
        if (warp.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            logger.error(warp.resp.toString());
            return warp.resp;
        }

        String result;
        try {
            result = blockchainService.deployEvidenceContract(warp.keyPair);
            warp.resp.setData(result);
        } catch (Exception e) {
            warp.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_DEPLOY_ERR);
            warp.resp.setData(e.getMessage());
        }
        return warp.resp;
    }

    @PostMapping("/receipt/save_receipt")
    public RespBody<String> saveReceipt(
            @RequestHeader String chainAccountInfo, @RequestBody SaveReceiptRequest request) {
        ContextEvidence context = prepareContextEvidence(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        BigInteger tokenId = request.getTokenId();
        String txId = request.getTxId();
        String ext = request.getExt();
        TransactionReceipt receipt = context.contract.saveReceipt(tokenId, txId, ext);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/receipt/batch_save_receipt")
    public RespBody<String> batchSaveReceipt(
            @RequestHeader String chainAccountInfo, @RequestBody BatchSaveReceiptRequest request) {
        ContextEvidence context = prepareContextEvidence(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        BigInteger startTokenId = request.getStartTokenId();
        BigInteger count = request.getCount();
        String txId = request.getTxId();
        String ext = request.getExt();
        TransactionReceipt receipt =
                context.contract.batchSaveReceipt(startTokenId, count, txId, ext);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
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

    private ContextNFT prepareContextNFT(String chainAccountInfo, Request request) {
        RespBody<String> resp = new RespBody<>(NFTResponseInfo.SUCCESS);
        ContextNFT context = new ContextNFT(null, resp);

        if (false == request.check()) {
            resp.setNFTResponseInfo(NFTResponseInfo.INVALID_REQUEST);
            resp.setData(request.toJsonString());
            logger.error(resp.toString());
            return context;
        }

        ChainAccountInfo accountInfo;
        try {
            accountInfo = parseChainAccountInfo(chainAccountInfo);
        } catch (Exception e) {
            resp.setNFTResponseInfo(NFTResponseInfo.INVALID_ACCOUNT);
            resp.setData(e.getMessage());
            logger.error(resp.toString());
            return context;
        }

        String address = accountInfo.getAddress();
        String password = accountInfo.getPassword();
        String contract = request.getContractAddress();

        logger.debug("account:{} make request:{}", address, request.toJsonString());

        ChainAccount chainAccount;
        try {
            chainAccount = accountService.getChainAccount(address, password);
        } catch (Exception e) {
            resp.setNFTResponseInfo(NFTResponseInfo.GET_CHAIN_ACCOUNT_ERR);
            resp.setData(e.getMessage());
            logger.error(resp.toString());
            return context;
        }

        NFT nft = nftContractInstance(chainAccount.getCryptoKeyPair(), contract);
        if (null == nft) {
            resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_NOT_EXIST);
            resp.setData(contract);
            logger.error(resp.toString());
            return context;
        }

        context.contract = nft;
        return context;
    }

    private ContextEvidence prepareContextEvidence(String chainAccountInfo, Request request) {
        RespBody<String> resp = new RespBody<>(NFTResponseInfo.SUCCESS);
        ContextEvidence context = new ContextEvidence(null, resp);

        if (false == request.check()) {
            resp.setNFTResponseInfo(NFTResponseInfo.INVALID_REQUEST);
            resp.setData(request.toJsonString());
            logger.error(resp.toString());
            return context;
        }

        ChainAccountInfo accountInfo;
        try {
            accountInfo = parseChainAccountInfo(chainAccountInfo);
        } catch (Exception e) {
            resp.setNFTResponseInfo(NFTResponseInfo.INVALID_ACCOUNT);
            resp.setData(e.getMessage());
            logger.error(resp.toString());
            return context;
        }

        String address = accountInfo.getAddress();
        String password = accountInfo.getPassword();
        String contract = request.getContractAddress();

        logger.debug("account:{} make request:{}", address, request.toJsonString());

        ChainAccount chainAccount;
        try {
            chainAccount = accountService.getChainAccount(address, password);
        } catch (Exception e) {
            resp.setNFTResponseInfo(NFTResponseInfo.GET_CHAIN_ACCOUNT_ERR);
            resp.setData(e.getMessage());
            logger.error(resp.toString());
            return context;
        }

        Evidence evidence = evidenceContractInstance(chainAccount.getCryptoKeyPair(), contract);
        if (null == evidence) {
            resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_NOT_EXIST);
            resp.setData(contract);
            logger.error(resp.toString());
            return context;
        }

        context.contract = evidence;
        return context;
    }

    private ContextNFT prepareNFTQuery(Request request) {
        logger.debug("request:{}", request.toJsonString());

        RespBody<String> resp = new RespBody<>(NFTResponseInfo.SUCCESS);
        ContextNFT context = new ContextNFT(null, resp);

        if (false == request.check()) {
            resp.setNFTResponseInfo(NFTResponseInfo.INVALID_REQUEST);
            resp.setData(request.toJsonString());
            logger.error(resp.toString());
            return context;
        }

        String contract = request.getContractAddress();
        try {
            context.contract = getNFTContractForQuery(contract);
        } catch (Exception e) {
            resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_NOT_EXIST);
            resp.setData(e.getMessage());
        }

        return context;
    }

    private ContextEvidence prepareEvidenceQuery(Request request) {
        logger.debug("request:{}", request.toJsonString());

        RespBody<String> resp = new RespBody<>(NFTResponseInfo.SUCCESS);
        ContextEvidence context = new ContextEvidence(null, resp);

        if (false == request.check()) {
            resp.setNFTResponseInfo(NFTResponseInfo.INVALID_REQUEST);
            resp.setData(request.toJsonString());
            logger.error(resp.toString());
            return context;
        }

        String contract = request.getContractAddress();
        try {
            context.contract = getEvidenceContractForQuery(contract);
        } catch (Exception e) {
            resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_NOT_EXIST);
            resp.setData(e.getMessage());
        }

        return context;
    }

    @PostMapping("/set_base_uri")
    public RespBody<String> setBaseURI(
            @RequestHeader String chainAccountInfo, @RequestBody SetBaseURIRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        TransactionReceipt receipt = context.contract.setBaseURI(request.getBaseURI());
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/approve")
    public RespBody<String> approve(
            @RequestHeader String chainAccountInfo, @RequestBody ApproveRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        TransactionReceipt receipt =
                context.contract.approve(request.getTo(), request.getTokenId());
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/set_approval_for_all")
    public RespBody<String> setApprovalForAll(
            @RequestHeader String chainAccountInfo, @RequestBody SetApproveForAllRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String operator = request.getOperator();
        Boolean approved = request.getApproved();

        TransactionReceipt receipt = context.contract.setApprovalForAll(operator, approved);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/transfer_from")
    public RespBody<String> transferFrom(
            @RequestHeader String chainAccountInfo, @RequestBody TransferFromRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String from = request.getFrom();
        String to = request.getTo();
        BigInteger tokenId = request.getTokenId();

        TransactionReceipt receipt = context.contract.transferFrom(from, to, tokenId);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/transfer_from_async")
    public RespBody<String> transferFromAsync(
            @RequestHeader String chainAccountInfo, @RequestBody TransferFromRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String from = request.getFrom();
        String to = request.getTo();
        BigInteger tokenId = request.getTokenId();

        context.contract.transferFrom(from, to, tokenId, transferCallback);

        context.resp.setData("");
        return context.resp;
    }

    @PostMapping("/safe_transfer_from")
    public RespBody<String> safeTransferFrom(
            @RequestHeader String chainAccountInfo, @RequestBody TransferFromRequest request) {
        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String from = request.getFrom();
        String to = request.getTo();
        BigInteger tokenId = request.getTokenId();

        TransactionReceipt receipt = context.contract.safeTransferFrom(from, to, tokenId);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/safe_transfer_from_with_data")
    public RespBody<String> safeTransferFromWithData(
            @RequestHeader String chainAccountInfo, @RequestBody TransferFromRequest request) {
        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String from = request.getFrom();
        String to = request.getTo();
        BigInteger tokenId = request.getTokenId();
        byte[] data = request.getData().getBytes();

        TransactionReceipt receipt = context.contract.safeTransferFrom(from, to, tokenId, data);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/mint")
    public RespBody<String> mint(
            @RequestHeader String chainAccountInfo, @RequestBody MintRequest request) {
        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String to = request.getTo();
        BigInteger tokenId = request.getTokenId();
        String tokenURI = request.getTokenURI();

        Boolean exists = exists(context, tokenId);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        if (exists) {
            String respMsg =
                    "tokenId:"
                            + request.getTokenId()
                            + " existed on contract:"
                            + request.getContractAddress();
            context.resp.setNFTResponseInfo(NFTResponseInfo.TOKEN_ID_EXISTED);
            context.resp.setData(respMsg);
            return context.resp;
        }

        TransactionReceipt receipt = context.contract.mintWithTokenURI(to, tokenId, tokenURI);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/batch_mint")
    public RespBody<String> batchMint(
            @RequestHeader String chainAccountInfo, @RequestBody BatchMintRequest request) {
        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String to = request.getTo();
        BigInteger startTokenId = request.getStartTokenId();
        BigInteger amount = request.getAmount();
        List<String> tokenURIs = request.getTokenURIs();

        TransactionReceipt receipt =
                context.contract.batchMintWithTokenURIs(to, startTokenId, amount, tokenURIs);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @GetMapping("/{contract}/exists/{token_id}")
    public RespBody<String> exists(
            @PathVariable(value = "contract") String contract,
            @PathVariable(value = "token_id") BigInteger tokenId) {

        ContextNFT context = prepareNFTQuery(new TokenIdCommonRequest(contract, tokenId));
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        try {
            Boolean exists = context.contract.exists(tokenId);
            context.resp.setData(exists.toString());
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".exists() exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    private Boolean exists(ContextNFT context, BigInteger tokenId) {
        String contract = context.contract.getContractAddress();

        Boolean exists = false;
        try {
            exists = context.contract.exists(tokenId);
            logger.debug("contract:{}.tokenId:{} exists: {}", contract, tokenId.toString(), exists);
            return exists;
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".exists(" + tokenId + ") exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return exists;
    }

    /*
     * header.chainAccountInfo:{"password":"abcdef","address":"0x65de289666e04a5099fd86f8096c797eacb09ce9"}
     * header.requestJson: {"nftContractAddress":"0x9454df6c99494fae49d11510f0cc2aa4b22d0bca","to":"0x65de289666e04a5099fd86f8096c797eacb09ce9","tokenId":3,"name":"name","description":"my nft"}
     * */
    @PostMapping("/mint_with_data")
    public RespBody<String> mintWithData(
            @RequestHeader String chainAccountInfo,
            @RequestHeader String requestJson,
            @RequestBody MultipartFile file) {
        logger.info("mint_with_data request:{}", requestJson);

        MintExtRequest request = parseNFTMintInfo(requestJson);
        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        if (null == file) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.NFT_FILE_NULL);
            context.resp.setData("no file data");
            return context.resp;
        }

        Boolean exists = exists(context, request.getTokenId());
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        if (exists) {
            String respMsg =
                    "tokenId:"
                            + request.getTokenId().toString()
                            + " existed on contract:"
                            + request.getContractAddress();
            context.resp.setNFTResponseInfo(NFTResponseInfo.TOKEN_ID_EXISTED);
            context.resp.setData(respMsg);
            return context.resp;
        }

        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.DOWNLOAD_FILE_ERR);
            context.resp.setData(e.getMessage());
            return context.resp;
        }

        // fileURI: tokenId/sha256(nft.png)/nft.png
        // 4/865917c868bb471d3e2eb9093aa1f47692d8ef57ed532bc0324ea9824de08e9e/nft.png

        String fileName = file.getOriginalFilename();
        long tokenId = request.getTokenId().longValue();
        String fileURI;
        try {
            fileURI = metadataService.uploadFile(tokenId, fileName, fileData);
        } catch (Exception e) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.UPLOAD_FILE_ERR);
            context.resp.setData(e.getMessage());
            return context.resp;
        }

        // tokenURI: tokenId/sha256(metadata.json)/metadata.json
        // 4/3811103d67cd0746f5826a96c38ff4e259991b998f6605aaa8e14562760ce868/metadata.json
        String tokenURI;
        String metadata =
                generateMetadata(fileURI, tokenId, request.getName(), request.getDescription());
        try {
            tokenURI = metadataService.uploadMetadata(tokenId, metadata);
        } catch (Exception e) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.UPLOAD_METADATA_ERR);
            context.resp.setData(e.getMessage());
            return context.resp;
        }

        String to = request.getTo();
        TransactionReceipt receipt =
                context.contract.mintWithTokenURI(to, request.getTokenId(), tokenURI);

        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/burn")
    public RespBody<String> burn(
            @RequestHeader String chainAccountInfo, @RequestBody TokenIdCommonRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        BigInteger tokenId = request.getTokenId();

        TransactionReceipt receipt = context.contract.burn(tokenId);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    private NFT getNFTContractForQuery(String address) throws Exception {
        NFT nft = cacheForNFTQuery.get(address);
        if (null == nft) {
            try {
                nft =
                        blockchainService.loadNFTContract(
                                accountService.getReadonlyKeyPair(), address);
                nft.subscribeTransferEvent(transferEventCallback);
                cacheForNFTQuery.put(address, nft);
            } catch (ContractNotExistException e) {
                String msg = "contract[" + address + "] not exist, exception:" + e.getMessage();
                logger.error(msg);
                throw new Exception(msg);
            } catch (Exception e) {
                String msg = "load contract[" + address + "], exception:" + e.getMessage();
                logger.error(msg);
                throw new Exception(msg);
            }
        }

        return nft;
    }

    private Evidence getEvidenceContractForQuery(String address) throws Exception {
        Evidence evidence = cacheForEvidenceQuery.get(address);
        if (null == evidence) {
            try {
                evidence =
                        blockchainService.loadEvidenceContract(
                                accountService.getReadonlyKeyPair(), address);
                cacheForEvidenceQuery.put(address, evidence);
            } catch (ContractNotExistException e) {
                String msg = "contract[" + address + "] not exist, exception:" + e.getMessage();
                logger.error(msg);
                throw new Exception(msg);
            } catch (Exception e) {
                String msg = "load contract[" + address + "], exception:" + e.getMessage();
                logger.error(msg);
                throw new Exception(msg);
            }
        }

        return evidence;
    }

    private NFT nftContractInstance(CryptoKeyPair keyPair, String address) {
        NFT nft = null;
        try {
            nft = blockchainService.loadNFTContract(keyPair, address);
        } catch (ContractNotExistException e) {
            logger.warn("contract not exist:{}, exception:{}", address, e);
        } catch (Exception e) {
            logger.warn("load contract with address:{} exception:{}", address, e);
        }

        return nft;
    }

    private Evidence evidenceContractInstance(CryptoKeyPair keyPair, String address) {
        Evidence evidence = null;
        try {
            evidence = blockchainService.loadEvidenceContract(keyPair, address);
        } catch (ContractNotExistException e) {
            logger.warn("contract not exist:{}, exception:{}", address, e);
        } catch (Exception e) {
            logger.warn("load contract with address:{} exception:{}", address, e);
        }

        return evidence;
    }

    public RespBody<String> getTxIdsHandler(TokenIdCommonRequest request) {
        ContextEvidence context = prepareEvidenceQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String contract = request.getContractAddress();
        BigInteger tokenId = request.getTokenId();
        try {
            List<String> txIds = context.contract.getTxIds(tokenId);
            context.resp.setData(JSON.toJSONString(txIds));
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".getTxIds(" + tokenId + ") exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> getExtsHandler(TokenIdCommonRequest request) {
        ContextEvidence context = prepareEvidenceQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String contract = request.getContractAddress();
        BigInteger tokenId = request.getTokenId();
        try {
            List<String> exts = context.contract.getExts(tokenId);
            context.resp.setData(JSON.toJSONString(exts));
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".getExts(" + tokenId + ") exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> getCountHandler(TokenIdCommonRequest request) {
        ContextEvidence context = prepareEvidenceQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String contract = request.getContractAddress();
        BigInteger tokenId = request.getTokenId();
        try {
            BigInteger count = context.contract.getCount(tokenId);
            context.resp.setData(count.toString());
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".getCount(" + tokenId + ") exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> balanceOfHandler(BalanceOfRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        String owner = request.getOwner();
        try {
            BigInteger balance = context.contract.balanceOf(owner);
            context.resp.setData(balance.toString());
        } catch (Exception e) {
            String errMsg = "contract:" + address + ".balanceOf(" + owner + ") exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> nameHandler(CommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        try {
            String name = context.contract.name();
            context.resp.setData(name);
        } catch (Exception e) {
            String errMsg = "contract:" + address + ".name() exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> symbolHandler(@RequestBody CommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        try {
            String symbol = context.contract.symbol();
            context.resp.setData(symbol);
        } catch (Exception e) {
            String errMsg = "contract:" + address + ".symbol() exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> tokenURIHandler(TokenIdCommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String contract = request.getContractAddress();
        BigInteger tokenId = request.getTokenId();
        try {
            String tokenURI = context.contract.tokenURI(tokenId);
            context.resp.setData(tokenURI);
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".tokenURI(" + tokenId + ") exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> ownerOfHandler(TokenIdCommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String contract = request.getContractAddress();
        BigInteger tokenId = request.getTokenId();
        try {
            String owner = context.contract.ownerOf(tokenId);
            context.resp.setData(owner);
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".ownerOf(" + tokenId + ") exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> baseURIHandler(CommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String contract = request.getContractAddress();
        try {
            String baseURI = context.contract.baseURI();
            context.resp.setData(baseURI);
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".baseURI() exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> tokenOfOwnerByIndexHandler(
            @RequestBody TokenOfOwnerByIndexRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        String owner = request.getOwner();
        BigInteger index = request.getIndex();
        try {
            BigInteger tokenId = context.contract.tokenOfOwnerByIndex(owner, index);
            context.resp.setData(tokenId.toString());
        } catch (Exception e) {
            String errMsg =
                    "contract:"
                            + address
                            + ".tokenOfOwnerByIndex("
                            + request.getOwner()
                            + ", "
                            + request.getIndex()
                            + ") exception:"
                            + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> totalSupplyHandler(CommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String contract = request.getContractAddress();
        try {
            BigInteger totalSupply = context.contract.totalSupply();
            context.resp.setData(totalSupply.toString());
        } catch (Exception e) {
            String errMsg = "contract:" + contract + ".totalSupply() exception:" + e.getMessage();
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> tokenByIndexHandler(TokenByIndexRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String contract = request.getContractAddress();
        BigInteger index = request.getIndex();
        try {
            BigInteger tokenId = context.contract.tokenByIndex(index);
            context.resp.setData(tokenId.toString());
        } catch (Exception e) {
            String errMsg =
                    "contract:"
                            + contract
                            + ".tokenByIndex("
                            + request.getIndex()
                            + ") exception:"
                            + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> getApprovedHandler(TokenIdCommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        BigInteger tokenId = request.getTokenId();
        try {
            String approvedAddress = context.contract.getApproved(tokenId);
            context.resp.setData(approvedAddress);
        } catch (Exception e) {
            String errMsg =
                    "contract:"
                            + address
                            + ".getApproved("
                            + request.getTokenId()
                            + ") exception:"
                            + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> isApprovedForAllHandler(IsApprovedForAllRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        String owner = request.getOwner();
        String operator = request.getOperator();
        try {
            Boolean approved = context.contract.isApprovedForAll(owner, operator);
            context.resp.setData(approved.toString());
        } catch (Exception e) {
            String errMsg =
                    "contract:"
                            + address
                            + ".isApprovedForAll("
                            + request.getOwner()
                            + ", "
                            + request.getOperator()
                            + ") exception:"
                            + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> issueTransferAllowedHandler(CommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        try {
            Boolean issueTransferAllowed = context.contract.issueTransferAllowed();
            context.resp.setData(issueTransferAllowed.toString());
        } catch (Exception e) {
            String errMsg = "contract:" + address + ".issueTransferAllowed() exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> transferIntervalHandler(CommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        try {
            BigInteger transferInterval = context.contract.transferInterval();
            context.resp.setData(transferInterval.toString());
        } catch (Exception e) {
            String errMsg = "contract:" + address + ".transferInterval() exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> maxTransferCountHandler(CommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        try {
            BigInteger maxTransferCount = context.contract.maxTransferCount();
            context.resp.setData(maxTransferCount.toString());
        } catch (Exception e) {
            String errMsg = "contract:" + address + ".maxTransferCount() exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> transferCountHandler(TokenIdCommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        BigInteger tokenId = request.getTokenId();
        try {
            BigInteger transferCount = context.contract.transferCount(tokenId);
            context.resp.setData(transferCount.toString());
        } catch (Exception e) {
            String errMsg =
                    "contract:" + address + ".transferCount(" + tokenId + ") exception:" + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    public RespBody<String> lastTransferTimestampHandler(TokenIdCommonRequest request) {
        ContextNFT context = prepareNFTQuery(request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        String address = request.getContractAddress();
        BigInteger tokenId = request.getTokenId();
        try {
            BigInteger lastTransferTimestamp = context.contract.lastTransferTimestamp(tokenId);
            context.resp.setData(lastTransferTimestamp.toString());
        } catch (Exception e) {
            String errMsg =
                    "contract:"
                            + address
                            + ".lastTransferTimestamp("
                            + tokenId
                            + ") exception:"
                            + e;
            logger.error(errMsg);
            context.resp.setData(errMsg);
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_QUERY_ERR);
        }

        return context.resp;
    }

    @PostMapping("/set_issue_transfer_allowed")
    public RespBody<String> setIssueTransferAllowed(
            @RequestHeader String chainAccountInfo,
            @RequestBody SetIssueTransferAllowedRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        Boolean issueTransferAllowed = request.getIssueTransferAllowed();

        TransactionReceipt receipt = context.contract.setIssueTransferAllowed(issueTransferAllowed);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/set_transfer_interval")
    public RespBody<String> setTransferInterval(
            @RequestHeader String chainAccountInfo,
            @RequestBody SetTransferIntervalRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        BigInteger transferInterval = request.getTransferInterval();

        TransactionReceipt receipt = context.contract.setTransferInterval(transferInterval);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }

    @PostMapping("/set_max_transfer_count")
    public RespBody<String> setMaxTransferCount(
            @RequestHeader String chainAccountInfo,
            @RequestBody SetMaxTransferCountRequest request) {

        ContextNFT context = prepareContextNFT(chainAccountInfo, request);
        if (context.resp.getCode() != NFTResponseInfo.SUCCESS.getCode()) {
            return context.resp;
        }

        BigInteger maxTransferCount = request.getMaxTransferCount();

        TransactionReceipt receipt = context.contract.setMaxTransferCount(maxTransferCount);
        if (!receipt.isStatusOK()) {
            context.resp.setNFTResponseInfo(NFTResponseInfo.CONTRACT_CALL_ERR);
        }

        context.resp.setData(receipt.toString());
        return context.resp;
    }
}
