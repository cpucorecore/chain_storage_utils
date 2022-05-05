package com.ancun.chain_storage.contracts;

import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.*;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.eventsub.EventCallback;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class IERC721Metadata extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"approved\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"operator\",\"type\":\"address\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"approved\",\"type\":\"bool\"}],\"name\":\"ApprovalForAll\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"indexed\":true,\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"balance\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"}],\"name\":\"getApproved\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"operator\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"operator\",\"type\":\"address\"}],\"name\":\"isApprovedForAll\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"}],\"name\":\"ownerOf\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"owner\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"}],\"name\":\"safeTransferFrom\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"},{\"internalType\":\"bytes\",\"name\":\"data\",\"type\":\"bytes\"}],\"name\":\"safeTransferFrom\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"operator\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"_approved\",\"type\":\"bool\"}],\"name\":\"setApprovalForAll\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"bytes4\",\"name\":\"interfaceId\",\"type\":\"bytes4\"}],\"name\":\"supportsInterface\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"}],\"name\":\"tokenURI\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"tokenId\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_GETAPPROVED = "getApproved";

    public static final String FUNC_ISAPPROVEDFORALL = "isApprovedForAll";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNEROF = "ownerOf";

    public static final String FUNC_SAFETRANSFERFROM = "safeTransferFrom";

    public static final String FUNC_SETAPPROVALFORALL = "setApprovalForAll";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOKENURI = "tokenURI";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final Event APPROVAL_EVENT =
            new Event(
                    "Approval",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Uint256>(true) {}));
    ;

    public static final Event APPROVALFORALL_EVENT =
            new Event(
                    "ApprovalForAll",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Bool>() {}));
    ;

    public static final Event TRANSFER_EVENT =
            new Event(
                    "Transfer",
                    Arrays.<TypeReference<?>>asList(
                            new TypeReference<Address>(true) {},
                            new TypeReference<Address>(true) {},
                            new TypeReference<Uint256>(true) {}));
    ;

    protected IERC721Metadata(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses =
                new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.approved = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeApprovalEvent(
            String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(APPROVAL_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeApprovalEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(APPROVAL_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public List<ApprovalForAllEventResponse> getApprovalForAllEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(APPROVALFORALL_EVENT, transactionReceipt);
        ArrayList<ApprovalForAllEventResponse> responses =
                new ArrayList<ApprovalForAllEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ApprovalForAllEventResponse typedResponse = new ApprovalForAllEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.operator = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.approved = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeApprovalForAllEvent(
            String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(APPROVALFORALL_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeApprovalForAllEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(APPROVALFORALL_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList =
                extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses =
                new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void subscribeTransferEvent(
            String fromBlock, String toBlock, List<String> otherTopics, EventCallback callback) {
        String topic0 = eventEncoder.encode(TRANSFER_EVENT);
        subscribeEvent(ABI, BINARY, topic0, fromBlock, toBlock, otherTopics, callback);
    }

    public void subscribeTransferEvent(EventCallback callback) {
        String topic0 = eventEncoder.encode(TRANSFER_EVENT);
        subscribeEvent(ABI, BINARY, topic0, callback);
    }

    public TransactionReceipt approve(String to, BigInteger tokenId) {
        final Function function =
                new Function(
                        FUNC_APPROVE,
                        Arrays.<Type>asList(new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] approve(String to, BigInteger tokenId, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_APPROVE,
                        Arrays.<Type>asList(new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForApprove(String to, BigInteger tokenId) {
        final Function function =
                new Function(
                        FUNC_APPROVE,
                        Arrays.<Type>asList(new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getApproveInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_APPROVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public BigInteger balanceOf(String owner) throws ContractException {
        final Function function =
                new Function(
                        FUNC_BALANCEOF,
                        Arrays.<Type>asList(new Address(owner)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public String getApproved(BigInteger tokenId) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETAPPROVED,
                        Arrays.<Type>asList(new Uint256(tokenId)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Boolean isApprovedForAll(String owner, String operator) throws ContractException {
        final Function function =
                new Function(
                        FUNC_ISAPPROVEDFORALL,
                        Arrays.<Type>asList(new Address(owner), new Address(operator)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public String name() throws ContractException {
        final Function function =
                new Function(
                        FUNC_NAME,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String ownerOf(BigInteger tokenId) throws ContractException {
        final Function function =
                new Function(
                        FUNC_OWNEROF,
                        Arrays.<Type>asList(new Uint256(tokenId)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt safeTransferFrom(String from, String to, BigInteger tokenId) {
        final Function function =
                new Function(
                        FUNC_SAFETRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] safeTransferFrom(
            String from, String to, BigInteger tokenId, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SAFETRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSafeTransferFrom(
            String from, String to, BigInteger tokenId) {
        final Function function =
                new Function(
                        FUNC_SAFETRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, BigInteger> getSafeTransferFromAddressAddressUint256Input(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SAFETRANSFERFROM,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public TransactionReceipt safeTransferFrom(
            String from, String to, BigInteger tokenId, byte[] data) {
        final Function function =
                new Function(
                        FUNC_SAFETRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from),
                                new Address(to),
                                new Uint256(tokenId),
                                new DynamicBytes(data)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] safeTransferFrom(
            String from, String to, BigInteger tokenId, byte[] data, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SAFETRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from),
                                new Address(to),
                                new Uint256(tokenId),
                                new DynamicBytes(data)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSafeTransferFrom(
            String from, String to, BigInteger tokenId, byte[] data) {
        final Function function =
                new Function(
                        FUNC_SAFETRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from),
                                new Address(to),
                                new Uint256(tokenId),
                                new DynamicBytes(data)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple4<String, String, BigInteger, byte[]>
            getSafeTransferFromAddressAddressUint256BytesInput(
                    TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SAFETRANSFERFROM,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint256>() {},
                                new TypeReference<DynamicBytes>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, String, BigInteger, byte[]>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue(),
                (byte[]) results.get(3).getValue());
    }

    public TransactionReceipt setApprovalForAll(String operator, Boolean _approved) {
        final Function function =
                new Function(
                        FUNC_SETAPPROVALFORALL,
                        Arrays.<Type>asList(new Address(operator), new Bool(_approved)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] setApprovalForAll(
            String operator, Boolean _approved, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETAPPROVALFORALL,
                        Arrays.<Type>asList(new Address(operator), new Bool(_approved)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetApprovalForAll(String operator, Boolean _approved) {
        final Function function =
                new Function(
                        FUNC_SETAPPROVALFORALL,
                        Arrays.<Type>asList(new Address(operator), new Bool(_approved)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, Boolean> getSetApprovalForAllInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETAPPROVALFORALL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(
                (String) results.get(0).getValue(), (Boolean) results.get(1).getValue());
    }

    public Boolean supportsInterface(byte[] interfaceId) throws ContractException {
        final Function function =
                new Function(
                        FUNC_SUPPORTSINTERFACE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Bytes4(interfaceId)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public String symbol() throws ContractException {
        final Function function =
                new Function(
                        FUNC_SYMBOL,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String tokenURI(BigInteger tokenId) throws ContractException {
        final Function function =
                new Function(
                        FUNC_TOKENURI,
                        Arrays.<Type>asList(new Uint256(tokenId)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt transferFrom(String from, String to, BigInteger tokenId) {
        final Function function =
                new Function(
                        FUNC_TRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public byte[] transferFrom(
            String from, String to, BigInteger tokenId, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_TRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForTransferFrom(String from, String to, BigInteger tokenId) {
        final Function function =
                new Function(
                        FUNC_TRANSFERFROM,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(tokenId)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, BigInteger> getTransferFromInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_TRANSFERFROM,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public static IERC721Metadata load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new IERC721Metadata(contractAddress, client, credential);
    }

    public static IERC721Metadata deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                IERC721Metadata.class, client, credential, getBinary(client.getCryptoSuite()), "");
    }

    public static class ApprovalEventResponse {
        public TransactionReceipt.Logs log;

        public String owner;

        public String approved;

        public BigInteger tokenId;
    }

    public static class ApprovalForAllEventResponse {
        public TransactionReceipt.Logs log;

        public String owner;

        public String operator;

        public Boolean approved;
    }

    public static class TransferEventResponse {
        public TransactionReceipt.Logs log;

        public String from;

        public String to;

        public BigInteger tokenId;
    }
}
