package hyperledger.besu.java.client.ethconnecter.controller;

import hyperledger.besu.java.client.ethconnecter.model.Transaction;
import hyperledger.besu.java.client.ethconnecter.service.TransactionService;
import java.math.BigInteger;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

  @Autowired private TransactionService transactionService;
  private static final ArrayList<Transaction> transactionsArrayList = new ArrayList<>();

  static {
    Transaction transaction1 = new Transaction();
    transaction1.setId(1);
    transaction1.setRecipientAddress("recepientAddress1");
    transaction1.setSignature("signature1");
    transaction1.setData("data1");
    transactionsArrayList.add(transaction1);

    Transaction transaction2 = new Transaction();
    transaction2.setId(2);
    transaction2.setRecipientAddress("recepientAddress2");
    transaction2.setSignature("signature2");
    transaction2.setData("data2");
    transactionsArrayList.add(transaction2);
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseEntity<Object> createTransaction(@RequestBody Transaction transaction) {
    transactionsArrayList.add(transaction);
    return new ResponseEntity<>("Transaction is created successfully", HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}")
  public ResponseEntity<Object> getTransactionById(@PathVariable int id) {
    System.out.println("hello");
    for (Transaction transaction : transactionsArrayList) {
      if (transaction.getId() == id){
        return new ResponseEntity<>(transaction, HttpStatus.OK);
      }
    }
    return null;
  }

  // The Rest endpoint for decoding transaction by transaction(RLP encoded) in hexadecimal
  @GetMapping("/decode")
  public ResponseEntity<Object> decodeTransaction(
      @RequestParam("transaction_hexadecimal") @Validated String transactionHexString) {
    return new ResponseEntity<>(transactionService.decode(transactionHexString), HttpStatus.OK);
  }

  // The Rest endpoint for executing transaction which modify the state of the blockchain
  @PostMapping("/execute")
  public ResponseEntity<Object> executeTransaction(
      BigInteger gasPrice, BigInteger gasLimit, String contractAddress, String functionName) {
    return new ResponseEntity<>(
        transactionService.execute(gasPrice, gasLimit, contractAddress, functionName),
        HttpStatus.OK);
  }

  // The Rest endpoint for executing transaction which query the ledger, does not modify the state
  // of the blockchain
  @PostMapping("/call")
  public ResponseEntity<Object> call(String contractAddress, String functionName) {
    return new ResponseEntity<>(
        transactionService.call(contractAddress, functionName), HttpStatus.OK);
  }
}
