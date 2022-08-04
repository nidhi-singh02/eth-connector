package hyperledger.besu.java.client.ethconnecter.service;

import hyperledger.besu.java.client.ethconnecter.model.ClientResponseModel;
import org.springframework.http.ResponseEntity;

public interface ContractService {

  /**
   * Deploying smart contract on the network.
   *
   * @param contractBinary is the name of the contract binary
   * @return the address at which contract is deployed
   */
  ResponseEntity<ClientResponseModel> deployContract(String contractBinary);
}
