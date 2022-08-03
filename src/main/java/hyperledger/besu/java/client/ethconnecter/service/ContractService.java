package hyperledger.besu.java.client.ethconnecter.service;

public interface ContractService {

  /**
   * Deploying smart contract on the network.
   *
   * @param contractBinary is the name of the contract binary
   * @return the address at which contract is deployed
   */
  String deployContract(String contractBinary);
}
