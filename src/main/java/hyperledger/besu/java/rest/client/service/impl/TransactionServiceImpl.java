package hyperledger.besu.java.rest.client.service.impl;

import static hyperledger.besu.java.rest.client.exception.ErrorCode.HYPERLEDGER_BESU_SEND_TRANSACTION_ERROR;
import static hyperledger.besu.java.rest.client.exception.ErrorCode.HYPERLEDGER_BESU_TRANSACTION_ERROR;
import static hyperledger.besu.java.rest.client.exception.ErrorCode.HYPERLEDGER_BESU_TRANSACTION_RECEIPT_ERROR;
import static hyperledger.besu.java.rest.client.exception.ErrorCode.VALIDATION_FAILED;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.dto.Transaction;
import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import hyperledger.besu.java.rest.client.exception.ErrorConstants;
import hyperledger.besu.java.rest.client.exception.ServiceException;
import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import hyperledger.besu.java.rest.client.model.TransactionResponseModel;
import hyperledger.besu.java.rest.client.service.TransactionService;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.AbiDefinition;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Numeric;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

  @Autowired ObjectMapper objectMapper;

  private final EthConfig ethConfig;
  private final TransactionReceiptProcessor transactionReceiptProcessor;
  private final Credentials credentials;

  // these are default values found in the TransactionManager of Web3j
  private static final int DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH = 40;
  private static final long DEFAULT_POLLING_FREQUENCY = 15 * 1000;

  public TransactionServiceImpl(final EthConfig ethConfig, final Credentials credentials) {
    this.ethConfig = ethConfig;
    this.transactionReceiptProcessor =
        new PollingTransactionReceiptProcessor(
            ethConfig.getWeb3jList().get(0),
            DEFAULT_POLLING_FREQUENCY,
            DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
    this.credentials = credentials;
  }

  private BigInteger getNonce(String address) {
    EthGetTransactionCount ethGetTransactionCount;
    try {
      ethGetTransactionCount =
          ethConfig
              .getWeb3jList()
              .get(0)
              .ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
              .sendAsync()
              .get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
    log.debug("Generated nonce: {}", ethGetTransactionCount.getTransactionCount());
    return ethGetTransactionCount.getTransactionCount();
  }

  private EthSendTransaction sendTransaction(RawTransaction rawTransaction) {
    // Sign using the wallet that is initialized from the config
    // TODO: Change it to call a signer from the wallet
    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
    String hexMessage = Numeric.toHexString(signedMessage);
    log.debug("signed hex Message: {}" + hexMessage);

    // Send it to ethereum nodes
    EthSendTransaction ethSendTransaction;
    try {
      // pick one of the web3j object.
      // TODO: improve the logic to iterate through the list in case of a failure
      ethSendTransaction =
          ethConfig.getWeb3jList().get(0).ethSendRawTransaction(hexMessage).sendAsync().get();
    } catch (InterruptedException | ExecutionException e) {
      throw new ServiceException(HYPERLEDGER_BESU_SEND_TRANSACTION_ERROR, e.getMessage(), e);
    }

    // if there are errors, then convert it to an exception
    if (ethSendTransaction.hasError()) {
      throw new BesuTransactionException(
          HYPERLEDGER_BESU_SEND_TRANSACTION_ERROR, ethSendTransaction.getError().getMessage());
    }

    // return the response object
    return ethSendTransaction;
  }

  private String readTransaction(Transaction transaction) {
    EthCall response = null;
    try {
      response =
          ethConfig
              .getWeb3jList()
              .get(0)
              .ethCall(
                  org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                      credentials.getAddress(),
                      transaction.getContractAddress(),
                      transaction.getEncodedFunction()),
                  DefaultBlockParameterName.LATEST)
              .sendAsync()
              .get();
    } catch (Exception e) {
      throw new BesuTransactionException(HYPERLEDGER_BESU_TRANSACTION_ERROR, "unable to ethcall");
    }
    if (response.isReverted()) {
      throw new BesuTransactionException(HYPERLEDGER_BESU_TRANSACTION_ERROR, "failed in ethcall");
    }
    log.debug("response: {} raw response: {}", response, response.getRawResponse());
    log.debug("error: {}", response.getError());
    log.debug("result: {}", response.getResult());
    return response.getValue();
  }

  @Override
  public ClientResponseModel execute(
      MultipartFile abiDefinitionFile,
      String contractAddress,
      String functionName,
      String... params) {

    List<AbiDefinition> abiDefinitionList;
    try {
      abiDefinitionList =
          objectMapper.readValue(
              abiDefinitionFile.getBytes(),
              objectMapper
                  .getTypeFactory()
                  .constructCollectionType(List.class, AbiDefinition.class));
      log.debug("ABI definition: {}", abiDefinitionList);
    } catch (IOException e) {
      throw new ServiceException(
          VALIDATION_FAILED, "Unable to retrieve contents from abi definition file");
    }

    // The flow should be
    // 1. To create the payload for transaction
    // 2. Ge the payload signed by the right key, this is available in wallet
    // Note: The current code assumes certain parameters such as gasPrice
    // Ref: https://docs.web3j.io/4.8.7/transactions/wallet_files/
    Transaction transaction =
        Transaction.builder()
            .abiDefinitionList(abiDefinitionList)
            .contractAddress(contractAddress)
            .functionName(functionName)
            .params(params)
            .gasPrice(BigInteger.valueOf(ethConfig.getEthProperties().getGasPrice()))
            .gasLimit(BigInteger.valueOf(ethConfig.getEthProperties().getGasLimit()))
            .nonce(getNonce(credentials.getAddress()))
            .build();
    EthSendTransaction ethSendTransaction = sendTransaction(transaction.getRawTransaction());

    // so far, we are able to successfully send a transaction to the ethereum
    // network. But that does not mean it is picked up for execution.
    String transactionHash = ethSendTransaction.getTransactionHash();
    log.debug("raw response: {}" + ethSendTransaction.getRawResponse());
    log.info("waiting for transaction {} confirmation", transactionHash);

    // add a blocking call logic here to wait for the transaction receipt
    // for a given hash.
    TransactionReceipt transferTransactionReceipt;
    try {
      transferTransactionReceipt =
          transactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
    } catch (Exception e) {
      throw new BesuTransactionException(
          HYPERLEDGER_BESU_TRANSACTION_RECEIPT_ERROR, e.getMessage());
    }
    log.info("transaction found in block: {}", transferTransactionReceipt.getBlockNumber());

    // construct the response payload
    // TODO: rework on the output structure
    List<Log> logs = transferTransactionReceipt.getLogs();
    log.debug("logs size: {} logs: {}", logs.size(), logs.size());

    TransactionResponseModel resultObject =
        new TransactionResponseModel(
            transactionHash, transferTransactionReceipt.getBlockNumber(), Collections.emptyList());
    return new ClientResponseModel(ErrorConstants.NO_ERROR, resultObject);
  }

  @Override
  public ClientResponseModel read(
      MultipartFile abiDefinitionFile,
      String contractAddress,
      String functionName,
      String... params) {

    List<AbiDefinition> abiDefinitionList;
    try {
      abiDefinitionList =
          objectMapper.readValue(
              abiDefinitionFile.getBytes(),
              objectMapper
                  .getTypeFactory()
                  .constructCollectionType(List.class, AbiDefinition.class));
      log.debug("ABI definition: {}", abiDefinitionList);
    } catch (IOException e) {
      throw new ServiceException(
          VALIDATION_FAILED, "Unable to retrieve contents from abi definition file");
    }

    // The flow should be
    // 1. To create the payload for transaction
    // 2. Ge the payload signed by the right key, this is available in wallet
    // Note: The current code assumes certain parameters such as gasPrice
    // Ref: https://docs.web3j.io/4.8.7/transactions/wallet_files/
    Transaction transaction =
        Transaction.builder()
            .abiDefinitionList(abiDefinitionList)
            .contractAddress(contractAddress)
            .functionName(functionName)
            .params(params)
            .gasPrice(BigInteger.valueOf(ethConfig.getEthProperties().getGasPrice()))
            .gasLimit(BigInteger.valueOf(ethConfig.getEthProperties().getGasLimit()))
            .nonce(getNonce(credentials.getAddress()))
            .build();
    // read the response of transaction
    String response = readTransaction(transaction);
    log.info("responseValue: {} ", response);

    List<Type> result = transaction.getDecodedFunction(response);

    // Store the result into a custom object variable
    TransactionResponseModel resultObject = new TransactionResponseModel(result);
    return new ClientResponseModel(ErrorConstants.NO_ERROR, resultObject);
  }

  @Override
  public ClientResponseModel deploy(final String compiledHexString) {

    // Contract is deployed as a transaction as well
    Transaction transaction =
        Transaction.builder()
            .compiledHexBinary(compiledHexString)
            .gasPrice(BigInteger.valueOf(ethConfig.getEthProperties().getGasPrice()))
            .gasLimit(BigInteger.valueOf(ethConfig.getEthProperties().getGasLimit()))
            .nonce(getNonce(credentials.getAddress()))
            .build();
    EthSendTransaction ethSendTransaction = sendTransaction(transaction.getContractTransaction());

    String transactionHash = ethSendTransaction.getTransactionHash();
    log.info("transactionHash: {}", transactionHash);
    log.info("result: {}", ethSendTransaction.getResult());

    // add a blocking call logic here to wait for the transaction receipt
    // for a given hash.
    TransactionReceipt transferTransactionReceipt;
    try {
      transferTransactionReceipt =
          transactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
    } catch (Exception e) {
      throw new BesuTransactionException(
          HYPERLEDGER_BESU_TRANSACTION_RECEIPT_ERROR, e.getMessage());
    }
    log.info("transaction found in block: {}", transferTransactionReceipt.getBlockNumber());

    String contractAddress = transferTransactionReceipt.getContractAddress();
    log.info("contract is deployed at: {}", contractAddress);
    return new ClientResponseModel(ErrorConstants.NO_ERROR, contractAddress);
  }
}
