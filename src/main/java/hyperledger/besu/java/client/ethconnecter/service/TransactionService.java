package hyperledger.besu.java.client.ethconnecter.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import hyperledger.besu.java.client.ethconnecter.model.ClientResponseModel;
import org.springframework.http.ResponseEntity;
import org.web3j.abi.datatypes.Type;

public interface TransactionService {

  /**
   * Decoding transaction by transaction(RLP encoded) in hexadecimal
   *
   * @param transactionHex the transaction in hexadecimal format(RLP)
   * @return the transaction details after decoding
   */
  ResponseEntity<ClientResponseModel> decode(String transactionHex);

  /**
   * Execute function in the contract modifying the world state
   *
   * @param gasPrice the gas price for executing the transaction
   * @param gasLimit the gas limit
   * @param contractAddress the address at which contract is deployed
   * @param functionName the name of the function in the smart contract
   * @return the map containing transaction hash and block number
   */
  ResponseEntity<ClientResponseModel> execute(
      BigInteger gasPrice, BigInteger gasLimit, String contractAddress, String functionName);

  /**
   * Call function to query the state
   *
   * @param contractAddress the address at which contract is deployed
   * @param functionName the name of the function in the smart contract
   * @return List containing the response from the contract
   */
  ResponseEntity<ClientResponseModel> call(String contractAddress, String functionName);
}
