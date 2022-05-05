package com.ancun.chain_storage.controller;

import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

class TransferCallback extends TransactionCallback {
    private static Logger logger = LoggerFactory.getLogger(TransferCallback.class);

    public Semaphore semaphore = new Semaphore(1, true);

    TransferCallback() {
        try {
            semaphore.acquire(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onTimeout() {
        super.onTimeout();
        semaphore.release();
    }

    // wait until get the transactionReceipt
    @Override
    public void onResponse(TransactionReceipt receipt) {
        logger.info("----onResponse----: [{}]", receipt.toString());
        if (receipt.isStatusOK()) {
            logger.info("----success----");
        } else {
            logger.error("err={}", receipt.getMessage());
        }

        semaphore.release();
    }
}
