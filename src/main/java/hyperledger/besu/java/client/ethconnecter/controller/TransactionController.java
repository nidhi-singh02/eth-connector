package hyperledger.besu.java.client.ethconnecter.controller;

import hyperledger.besu.java.client.ethconnecter.service.TransactionService;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

  @Value("${gasPrice}")
  BigInteger gasPrice;
  @Value("${gasLimit}")
  BigInteger gasLimit;



  @Autowired
  TransactionService transactionService;

  // The Rest endpoint for decoding transaction by transaction(RLP encoded) in hexadecimal
  @GetMapping("/decode")
  public ResponseEntity<Object> decodeTransaction(
      @RequestParam("transaction_hexadecimal") @Validated String transactionHexString) {
    return new ResponseEntity<>(transactionService.decode(transactionHexString), HttpStatus.OK);
  }

  // The Rest endpoint for executing transaction which modify the state of the blockchain
  @PostMapping("/execute")
  public ResponseEntity<Object> executeTransaction(
          @RequestHeader String contractAddress, @RequestHeader String functionName) {
    return new ResponseEntity<>(
        transactionService.execute(this.gasPrice, this.gasLimit, contractAddress, functionName),
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
