package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.NFT;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.EventLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class TransferEventCallback implements EventCallback {
    private static Logger logger = LoggerFactory.getLogger(TransferEventCallback.class);

    private Semaphore semaphore = new Semaphore(1, true);
    private ABICodec abiCodec = new ABICodec(new CryptoSuite(1));
    private Set<String> logDedup = new HashSet<>();

    @Override
    public void onReceiveLog(int status, List<EventLog> logs) {
        try {
            semaphore.acquire(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (0 == status && null != logs) {
            for (int i = 0; i < logs.size(); i++) {
                try {
                    EventLog log = logs.get(i);
                    log.getBlockNumber();
                    log.getTransactionIndex();
                    log.getLogIndex();

                    // log可能重复，根据区块号+交易号+日志号去重
                    String key = log.getBlockNumber().toString() + "-" + log.getTransactionIndex().toString() + "-" + log.getLogIndex().toString();
                    if (logDedup.contains(key)) {
                        logger.warn("duplicated log:{}", key);
                        continue;
                    }
                    logDedup.add(key);

                    List<Object> list = abiCodec.decodeEvent(NFT.ABI, "Transfer", log);
                    if (3 != list.size()) {
                        logger.error("wrong log:{}", log);
                        continue;
                    }

                    String tokenIdString = list.get(2).toString();
                    BigInteger tokenId = new BigInteger(tokenIdString.substring(2), 16);
                    logger.info("from:{}, to:{}, tokenId:{}", "0x" + list.get(0).toString().substring(26), "0x" + list.get(1).toString().substring(26), tokenId);
                } catch (ABICodecException e) {
                    logger.error(e.toString());
                }
            }
        }

        semaphore.release();
    }
}
