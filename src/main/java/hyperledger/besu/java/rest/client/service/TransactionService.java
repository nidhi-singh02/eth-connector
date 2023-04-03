package hyperledger.besu.java.rest.client.service;

import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import org.springframework.web.multipart.MultipartFile;

public interface TransactionService {

  /** */
  ClientResponseModel execute(
      MultipartFile abiDefinitionFile,
      String contractAddress,
      String functionName,
      String... params);

  /** */
  ClientResponseModel read(
      MultipartFile abiDefinitionFile,
      String contractAddress,
      String functionName,
      String... params);

  /** */
  ClientResponseModel deploy(final String compiledHexString);
}
