package hyperledger.besu.java.rest.client.controller;

import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import hyperledger.besu.java.rest.client.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class EthContractController {

  @Autowired private TransactionService transactionService;

  /** */
  @PostMapping(value = "/contracts/deploy")
  public ResponseEntity<ClientResponseModel> deploy(@Validated final String contractBinary) {
    log.debug("Deploying the contract {}", contractBinary);
    return new ResponseEntity<>(transactionService.deploy(contractBinary), HttpStatus.OK);
  }
}
