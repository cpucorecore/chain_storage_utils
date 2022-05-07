package com.ancun.chain_storage.service_blockchain.impl;

public class InvalidResolverAddressException extends Exception {
    public InvalidResolverAddressException(String resolverAddress) {
        super("invalid resolver address [" + resolverAddress + "]");
    }
}
