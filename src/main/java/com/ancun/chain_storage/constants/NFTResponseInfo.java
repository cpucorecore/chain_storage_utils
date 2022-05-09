package com.ancun.chain_storage.constants;

import static com.ancun.chain_storage.requests.RequestUtils.MAX_PASSWORD_LENGTH;

public enum NFTResponseInfo {
    SUCCESS(0, "成功"),

    INVALID_REQUEST(1000, "invalid request"),
    NFT_FILE_NULL(1001, "no nft file"),

    // 链上账号相关
    INVALID_PASSWORD(2000, "密码不符合要求，不能为空，长度长度不能超过:" + MAX_PASSWORD_LENGTH),
    INVALID_ACCOUNT(2001, "账户信息不符合要求，地址长度必须为42, 密码不能为空，密码最大长度255"),

    CREATE_CHAIN_ACCOUNT_ERR(3000, "创建链上账号失败"),
    GET_CHAIN_ACCOUNT_ERR(3001, "获取链上账号失败"),

    // 合约相关
    CONTRACT_NOT_EXIST(4000, "合约不存在"),
    CONTRACT_DEPLOY_ERR(4001, "部署合约失败"),
    CONTRACT_QUERY_ERR(4002, "合约查询失败"),
    CONTRACT_CALL_ERR(4003, "合约执行失败"),
    TOKEN_ID_NOT_EXIST(4004, "tokenId not exist"),
    TOKEN_ID_EXISTED(4005, "tokenId existed"),

    // 文件服务相关
    DOWNLOAD_FILE_ERR(5000, "download NFT image by http failed"),
    UPLOAD_FILE_ERR(5001, "upload NFT image to ipfs failed"),
    UPLOAD_METADATA_ERR(5002, "upload NFT metadata to ipfs failed"),

    CS_Setting_Unknown_key(10001, "unknown setting key"),
    CS_Read_Contract_Exception(11002, "read contract exception"),
    CS_Write_Contract_Exception(11003, "write contract exception"),

    CONTRACT_EXCEPTION(13000, "contract exception"),

    CS_Invalid_Resolver_Address_Exception(12001, "invalid resolver address, it must be set"),
    ;

    private int code;
    private String msg;

    NFTResponseInfo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
