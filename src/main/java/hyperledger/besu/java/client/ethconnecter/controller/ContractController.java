package hyperledger.besu.java.client.ethconnecter.controller;

import hyperledger.besu.java.client.ethconnecter.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/contract")
public class ContractController {

  private ContractService contractService;

  // The Rest endpoint for deploying smart contract
  @PostMapping("/deploy")
  public ResponseEntity<String> deploy(@Validated String contractBinary) {
    System.out.println(contractBinary);
    return new ResponseEntity<>(contractService.deployContract(contractBinary), HttpStatus.OK);
  }
}
