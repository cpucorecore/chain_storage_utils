package com.ancun.chain_storage.service_blockchain.impl;

public class ContractNotExistException extends Exception {
    public ContractNotExistException(String address) {
        super("contract[" + address + "] not exist");
    }
}
