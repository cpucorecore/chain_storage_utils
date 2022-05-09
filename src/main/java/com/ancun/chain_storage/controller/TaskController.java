package com.ancun.chain_storage.controller;

import com.ancun.chain_storage.contracts.Node;
import com.ancun.chain_storage.contracts.Task;
import com.ancun.chain_storage.model.RespBody;
import com.ancun.chain_storage.service_blockchain.BlockchainService;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple8;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.math.BigInteger;

import static com.ancun.chain_storage.constants.NFTResponseInfo.CONTRACT_EXCEPTION;
import static com.ancun.chain_storage.constants.NFTResponseInfo.SUCCESS;

@RestController
@RequestMapping("chain_storage/task")
public class TaskController {
    private Logger logger = LoggerFactory.getLogger(NodeController.class);

    @Resource
    private BlockchainService blockchainService;

    @GetMapping("get_task/{tid}")
    public RespBody<String> handleGetTask(@PathVariable(value = "tid") BigInteger tid) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        Task task = null;
        try {
            task = blockchainService.loadTaskContract();
        } catch (ContractException e) {
            logger.warn("loadTaskContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Tuple5<String, BigInteger, String, BigInteger, String> taskItem;
        try {
            taskItem = task.getTask(tid);
        } catch (ContractException e) {
            logger.warn("task.getTask({}) exception:{}", tid.toString(10), e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(taskItem.toString());
        return resp;
    }

    @GetMapping("get_task_state/{tid}")
    public RespBody<String> handleGetTaskState(@PathVariable(value = "tid") BigInteger tid) {
        RespBody<String> resp = new RespBody<>(SUCCESS);
        Task task = null;
        try {
            task = blockchainService.loadTaskContract();
        } catch (ContractException e) {
            logger.warn("loadTaskContract exception:{}", e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        Tuple8<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> taskState;
        try {
            taskState = task.getTaskState(tid);
        } catch (ContractException e) {
            logger.warn("task.getTaskState({}) exception:{}", tid.toString(10), e.toString());
            resp.setNFTResponseInfo(CONTRACT_EXCEPTION);
            resp.setData(e.getMessage());
            return resp;
        }

        resp.setData(taskState.toString());
        return resp;
    }
}
