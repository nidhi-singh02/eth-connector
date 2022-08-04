package hyperledger.besu.java.client.ethconnecter.service.impl;

import hyperledger.besu.java.client.ethconnecter.exception.ErrorCode;
import hyperledger.besu.java.client.ethconnecter.exception.ErrorConstants;
import hyperledger.besu.java.client.ethconnecter.exception.ServiceException;
import hyperledger.besu.java.client.ethconnecter.model.ClientResponseModel;
import hyperledger.besu.java.client.ethconnecter.model.TransactionResponseModel;
import hyperledger.besu.java.client.ethconnecter.service.TransactionService;
import hyperledger.besu.java.client.ethconnecter.util.HelperModule;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.SignedRawTransaction;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

@Slf4j
@Service
public class TransactionImpl implements TransactionService {

  @Value("${gasLimit}")
  BigInteger gasLimit;
  @Value("${gasPrice}")
  BigInteger gasPrice;


  static EthSendTransaction validateTransaction(RawTransaction rawTransaction, Web3j web3j)
      throws Exception {
    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, HelperModule.CREDENTIALS);
    String hexMessage = Numeric.toHexString(signedMessage);
    System.out.println("hex Message: " + hexMessage);

    EthSendTransaction ethSendTransaction;
    try {
      ethSendTransaction = web3j.ethSendRawTransaction(hexMessage).sendAsync().get();
    } catch (InterruptedException | ExecutionException e) {
      throw new ServiceException(ErrorCode.HYPERLEDGER_BESU_SEND_TRANSACTION_ERROR, e.getMessage(), e);
    }
    if (ethSendTransaction.hasError()) {
      System.out.println(
          "error: "
              + ethSendTransaction.getError().getMessage()
              + " code: "
              + ethSendTransaction.getError().getCode());

      throw new Exception(ethSendTransaction.getError().getMessage());
    }
    return ethSendTransaction;
  }

//  public ResponseEntity<ClientResponseModel> decode(String transactionHex) {
//    final RawTransaction rawTransaction = TransactionDecoder.decode(transactionHex);
//    Map<String, String> transactionDetails = new HashMap<>();
//    try {
//      transactionDetails.put("type", String.valueOf(rawTransaction.getType()));
//      transactionDetails.put("nonce", String.valueOf(rawTransaction.getNonce()));
//      transactionDetails.put("to", rawTransaction.getTo());
//      transactionDetails.put("value", String.valueOf(rawTransaction.getValue()));
//      transactionDetails.put("gasPrice", String.valueOf(rawTransaction.getGasPrice()));
//      transactionDetails.put("gasLimit", String.valueOf(rawTransaction.getGasLimit()));
//      transactionDetails.put("data", rawTransaction.getData());
//      if (rawTransaction instanceof SignedRawTransaction) {
//
//        SignedRawTransaction signedResult = (SignedRawTransaction) rawTransaction;
//        Sign.SignatureData signatureData = signedResult.getSignatureData();
//        transactionDetails.put("sign data", String.valueOf(signatureData));
//        try {
//          transactionDetails.put("from", signedResult.getFrom());
//        } catch (SignatureException e) {
//          throw new RuntimeException(e);
//        }
//        transactionDetails.put("chainID", String.valueOf(signedResult.getChainId()));
//      }
//    } catch (BesuTransactionException e) {
//      log.error(
//              "Action Failed: A problem occurred while retrieving the transaction from the Ledger", e);
//      throw new BesuTransactionException(
//              ErrorCode.HYPERLEDGER_BESU_TRANSACTION_ERROR, e.getMessage(), e);
//    }
//
//    return new ResponseEntity<>(
//            new ClientResponseModel(ErrorConstants.NO_ERROR, resultString), HttpStatus.OK);
//  }

  public ResponseEntity<ClientResponseModel> decode(String transactionHex) {
    final RawTransaction rawTransaction = TransactionDecoder.decode(transactionHex);
    Map<String, String> transactionDetails = new HashMap<>();
    transactionDetails.put("type", String.valueOf(rawTransaction.getType()));
    transactionDetails.put("nonce", String.valueOf(rawTransaction.getNonce()));
    transactionDetails.put("to", rawTransaction.getTo());
    transactionDetails.put("value", String.valueOf(rawTransaction.getValue()));
    transactionDetails.put("gasPrice", String.valueOf(rawTransaction.getGasPrice()));
    transactionDetails.put("gasLimit", String.valueOf(rawTransaction.getGasLimit()));
    transactionDetails.put("data", rawTransaction.getData());

    if (rawTransaction instanceof SignedRawTransaction) {

      SignedRawTransaction signedResult = (SignedRawTransaction) rawTransaction;
      Sign.SignatureData signatureData = signedResult.getSignatureData();
      transactionDetails.put("sign data", String.valueOf(signatureData));
      try {
        transactionDetails.put("from", signedResult.getFrom());
      } catch (SignatureException e) {
        throw new ServiceException(ErrorCode.HYPERLEDGER_BESU_SIGNATURE_ERROR, e.getMessage(), e);
      }
      transactionDetails.put("chainID", String.valueOf(signedResult.getChainId()));
    }
    TransactionResponseModel resultObject = new TransactionResponseModel(transactionDetails);
    return new ResponseEntity<>(
            new ClientResponseModel(ErrorConstants.NO_ERROR, (Serializable) transactionDetails), HttpStatus.OK);
  }

  public ResponseEntity<ClientResponseModel> execute(
          BigInteger gasPrice, BigInteger gasLimit, String contractAddress, String functionName) {
    BigInteger nonce = HelperModule.getNonce(HelperModule.CREDENTIALS.getAddress());
    System.out.println("nonce: " + nonce);
    Function function = HelperModule.createContractFunction(functionName);
    String encodedFunction = FunctionEncoder.encode(function);

    RawTransaction rawTransaction =
        RawTransaction.createTransaction(
            nonce, this.gasPrice, this.gasLimit, contractAddress, encodedFunction);
    EthSendTransaction ethSendTransaction ;
    try {
      ethSendTransaction = validateTransaction(rawTransaction, HelperModule.web3j);
    } catch (Exception e) {
      throw new ServiceException(ErrorCode.HYPERLEDGER_BESU_VALIDATE_TRANSACTION_ERROR, e.getMessage(), e);
    }
    String transactionHash = ethSendTransaction.getTransactionHash();
    System.out.println("transactionHash: " + transactionHash); // result is same as transaction hash
    System.out.println("raw response: " + ethSendTransaction.getRawResponse());

    TransactionReceipt transferTransactionReceipt1;
    try {
      transferTransactionReceipt1 = HelperModule.waitForTransactionReceipt(transactionHash);
    } catch (Exception e) {
      throw new ServiceException(ErrorCode.HYPERLEDGER_BESU_TRANSACTION_RECEIPT_ERROR, e.getMessage(), e);
    }
    System.out.println(transferTransactionReceipt1.getTransactionHash() + " " + transactionHash);
    System.out.println("block: " + transferTransactionReceipt1.getBlockNumber());

    if (transferTransactionReceipt1.getTransactionHash().equals(transactionHash)) {
      System.out.println("transaction hash matches");
    }

    List<Log> logs = transferTransactionReceipt1.getLogs();

    Map<String, String> transactionDetails = new HashMap<>();
    transactionDetails.put("transactionHash", transactionHash);
    transactionDetails.put(
        "blockNumber", String.valueOf(transferTransactionReceipt1.getBlockNumber()));

    // Log log = logs.get(0);
    System.out.println("logs" + logs + logs.size());
//    return transactionDetails;
    TransactionResponseModel resultObject = new TransactionResponseModel(transactionDetails, null);
    return new ResponseEntity<>(
            new ClientResponseModel(ErrorConstants.NO_ERROR, (Serializable) resultObject), HttpStatus.OK);
  }

  public ResponseEntity<ClientResponseModel> call(String contractAddress, String functionName) {
    Function function = HelperModule.createContractFunction(functionName);
    System.out.println("function: " + function.getName());

    String responseValue;
    try {
      responseValue = HelperModule.callSmartContractFunction(function, contractAddress);
    } catch (Exception e) {
      throw new ServiceException(ErrorCode.HYPERLEDGER_BESU_CALL_SMART_CONTRACT_ERROR, e.getMessage(), e);
    }

    List<Type> uint = FunctionReturnDecoder.decode(responseValue, function.getOutputParameters());

    System.out.println("responseValue: " + responseValue);
    System.out.println("uint: " + uint.size());

    TransactionResponseModel resultObject = new TransactionResponseModel(uint);
    return new ResponseEntity<>(
            new ClientResponseModel(ErrorConstants.NO_ERROR, (Serializable) resultObject), HttpStatus.OK);
  }
}
