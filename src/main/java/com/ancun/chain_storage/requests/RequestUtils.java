package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.math.BigInteger;

public class RequestUtils {
    public static final String keyAddress = "address";
    public static final String keyPassword = "password";

    public static final int ADDRESS_LENGTH = 42;
    public static final int TX_ID_LENGTH = 66;
    public static final int MAX_PASSWORD_LENGTH = 64;

    public static final int MAX_NFT_NAME_LENGTH = 256;
    public static final int MAX_NFT_SYMBOL_LENGTH = 64;
    public static final int MAX_BASE_URI_LENGTH = 256;
    public static final int MAX_TOKEN_URI_LENGTH = MAX_BASE_URI_LENGTH + 512;

    public static final int MAX_NFT_METADATA_NAME_LENGTH = 256;
    public static final int MAX_METADATA_DESCRIPTION_LENGTH = 1024;

    public static ChainAccountInfo parseChainAccountInfo(String accountInfo) throws Exception {
        JSONObject json = JSON.parseObject(accountInfo);
        String address = json.getString(keyAddress);
        String password = json.getString(keyPassword);

        if (!checkAddress(address)) {
            throw new Exception("invalid address:[" + address + "]");
        }
        if (!checkPassword(password)) {
            throw new Exception("invalid password:[" + password + "]");
        }

        ChainAccountInfo chainAccountInfo = new ChainAccountInfo(address, password);
        return chainAccountInfo;
    }

    public static MintExtRequest parseNFTMintInfo(String nftMintInfo) {
        JSONObject json = JSON.parseObject(nftMintInfo);

        String nftContractAddress = json.getString("nftContractAddress");
        String to = json.getString("to");
        BigInteger tokenId = json.getBigInteger("tokenId");
        String name = json.getString("name");
        String description = json.getString("description");

        return new MintExtRequest(nftContractAddress, to, tokenId, name, description);
    }

    public static Boolean checkPassword(String password) {
        if (null == password || "".equals(password)) {
            return false;
        }

        return password.length() <= MAX_PASSWORD_LENGTH;
    }

    public static Boolean checkAddress(String address) {
        if (null == address || "".equals(address)) {
            return false;
        }

        return address.length() == ADDRESS_LENGTH;
    }

    public static Boolean checkTxId(String txId) {
        if (null == txId || "".equals(txId)) {
            return false;
        }

        return txId.length() == TX_ID_LENGTH;
    }

    public static String generateMetadata(
            String uri, long tokenID, String name, String description) {
        Metadata metadata = new Metadata(name, tokenID, description, uri);
        return JSON.toJSONString(metadata);
    }

    public static Boolean nonNegative(BigInteger value) {
        return (1 == value.compareTo(BigInteger.valueOf(-1)));
    }
}
