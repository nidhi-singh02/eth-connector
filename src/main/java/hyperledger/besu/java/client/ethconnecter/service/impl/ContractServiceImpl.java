package hyperledger.besu.java.client.ethconnecter.service.impl;

import hyperledger.besu.java.client.ethconnecter.exception.ErrorCode;
import hyperledger.besu.java.client.ethconnecter.exception.ErrorConstants;
import hyperledger.besu.java.client.ethconnecter.exception.ServiceException;
import hyperledger.besu.java.client.ethconnecter.model.ClientResponseModel;
import hyperledger.besu.java.client.ethconnecter.service.ContractService;
import hyperledger.besu.java.client.ethconnecter.util.HelperModule;
import java.math.BigInteger;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Service
public class ContractServiceImpl implements ContractService {
  @Value("${gasLimit}")
  BigInteger gasLimit;
  @Value("${gasPrice}")
  BigInteger gasPrice;
  public ResponseEntity<ClientResponseModel> deployContract(String binaryName) {
    BigInteger nonce = HelperModule.getNonce(HelperModule.CREDENTIALS.getAddress());
    try {
      System.out.println("nonce: " + nonce);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    // to - address of the receiver. To deploy a contract, set to null
    RawTransaction rawTransaction;
    try {
      rawTransaction =
          RawTransaction.createContractTransaction( // requires a string
              nonce,
              gasPrice,
              gasLimit,
              BigInteger.ZERO,
//              HelperModule.getSolidityBinary(binaryName));
              binaryName);
    } catch (Exception e) {
      throw new ServiceException(ErrorCode.HYPERLEDGER_BESU_CREATE_RAW_TRANSACTION_ERROR, e.getMessage(), e);
    }
    EthSendTransaction ethSendTransaction;
    try {
      ethSendTransaction = TransactionImpl.validateTransaction(rawTransaction, HelperModule.web3j);
    } catch (Exception e) {
      throw new ServiceException(ErrorCode.HYPERLEDGER_BESU_SEND_TRANSACTION_ERROR, e.getMessage(), e);
    }
    System.out.println("result: " + ethSendTransaction.getResult());
    String transactionHash = ethSendTransaction.getTransactionHash();
    System.out.println("transactionHash: " + transactionHash);

    TransactionReceipt transactionReceipt;
    try {
      transactionReceipt = HelperModule.waitForTransactionReceipt(transactionHash);
    } catch (Exception e) {
      throw new ServiceException(ErrorCode.HYPERLEDGER_BESU_TRANSACTION_RECEIPT_ERROR, e.getMessage(), e);
    }
    if (Objects.equals(transactionReceipt.getTransactionHash(), transactionHash)) {
      System.out.println("Transaction hash matches");
    }

    String contractAddress = transactionReceipt.getContractAddress();
    System.out.println("contract is deployed at: " + contractAddress);
    String resultString = contractAddress;
    return new ResponseEntity<>(
            new ClientResponseModel(ErrorConstants.NO_ERROR, resultString), HttpStatus.OK);
  }
}
