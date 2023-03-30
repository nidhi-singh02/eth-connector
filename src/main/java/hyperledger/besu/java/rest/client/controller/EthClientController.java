package hyperledger.besu.java.rest.client.controller;

import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import hyperledger.besu.java.rest.client.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class EthClientController {

  @Autowired TransactionService transactionService;

  /** */
  @PostMapping(value = "/transactions")
  public ResponseEntity<ClientResponseModel> executeTransaction(
      @RequestParam("abi-definition") @Validated MultipartFile abiDefinitionFile,
      @RequestParam("transaction_contract_address") @Validated String contractAddress,
      @RequestParam("transaction_function_name") @Validated String functionName,
      @RequestParam("transaction_params") @Validated String... params) {
    log.debug(
        "Write transaction contract: {}, function: {}, params: {}",
        contractAddress,
        functionName,
        params);
    return new ResponseEntity<>(
        transactionService.execute(abiDefinitionFile, contractAddress, functionName, params),
        HttpStatus.OK);
  }

  /** */
  @GetMapping(value = "/transactions")
  public ResponseEntity<ClientResponseModel> readTransaction(
      @RequestParam("abi-definition") MultipartFile abiDefinitionFile,
      @RequestParam("transaction_contract_address") @Validated String contractAddress,
      @RequestParam("transaction_function_name") @Validated String functionName,
      @RequestParam("transaction_params") @Validated String... params) {
    log.debug(
        "Read transaction contract: {}, function: {}, params: {}",
        contractAddress,
        functionName,
        params);
    return new ResponseEntity<>(
        transactionService.read(abiDefinitionFile, contractAddress, functionName, params),
        HttpStatus.OK);
  }
}
