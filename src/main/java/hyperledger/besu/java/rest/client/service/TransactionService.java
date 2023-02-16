package hyperledger.besu.java.rest.client.service;

import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import java.util.List;
import org.web3j.protocol.core.methods.response.AbiDefinition;

public interface TransactionService {

  /** */
  ClientResponseModel execute(
      List<AbiDefinition> abiDefinitionList,
      String contractAddress,
      String functionName,
      String... params);

  /** */
  ClientResponseModel read(
      List<AbiDefinition> abiDefinitionList,
      String contractAddress,
      String functionName,
      String... params);

  /** */
  ClientResponseModel deploy(final String compiledHexString);
}
