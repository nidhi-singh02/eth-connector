package hyperledger.besu.java.client.ethconnecter.controller;

import hyperledger.besu.java.client.ethconnecter.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("contract")
public class ContractController {

  private ContractService contractService;

  /**
   * The Rest endpoint for deploying smart contarct
   *
   * @return responseEntity ResponseEntity Transaction Response
   */
  @PostMapping("/deploy")
  public ResponseEntity<String> deploy() {
    return new ResponseEntity<>(contractService.deployContract(), HttpStatus.OK);
  }
}
