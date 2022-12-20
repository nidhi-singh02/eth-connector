package hyperledger.besu.java.rest.client.controller;

import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import hyperledger.besu.java.rest.client.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class EthClientController {

  @Autowired TransactionService transactionService;

  /** */
  @PostMapping(value = "/transactions")
  public ResponseEntity<ClientResponseModel> executeTransaction(
      @RequestParam("transaction_contract_address") @Validated String contractAddress,
      @RequestParam("transaction_function_name") @Validated String functionName,
      @RequestParam("transaction_params") @Validated String... params) {
    log.debug(
        "Write transaction contract: {}, function: {}, params: {}",
        contractAddress,
        functionName,
        params);
    return new ResponseEntity<>(
        transactionService.execute(contractAddress, functionName, params), HttpStatus.OK);
  }

  /** */
  @GetMapping(value = "/transactions")
  public ResponseEntity<ClientResponseModel> readTransaction(
      @RequestParam("transaction_contract_address") @Validated String contractAddress,
      @RequestParam("transaction_function_name") @Validated String functionName,
      @RequestParam("transaction_params") @Validated String... params) {
    log.debug(
        "Read transaction contract: {}, function: {}, params: {}",
        contractAddress,
        functionName,
        params);
    return new ResponseEntity<>(
        transactionService.read(contractAddress, functionName, params), HttpStatus.OK);
  }
}
