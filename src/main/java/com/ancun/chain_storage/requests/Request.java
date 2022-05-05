package com.ancun.chain_storage.requests;

public interface Request {
    boolean check();

    String toJsonString();

    String getContractAddress();
}
