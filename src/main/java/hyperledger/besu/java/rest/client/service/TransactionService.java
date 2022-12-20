package hyperledger.besu.java.rest.client.service;

import hyperledger.besu.java.rest.client.model.ClientResponseModel;

public interface TransactionService {

  /** */
  ClientResponseModel execute(String contractAddress, String functionName, String... params);

  /** */
  ClientResponseModel read(String contractAddress, String functionName, String... params);

  /** */
  ClientResponseModel deploy(final String compiledHexString);
}
