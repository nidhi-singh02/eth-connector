package hyperledger.besu.java.rest.client.service;

import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import org.springframework.http.ResponseEntity;

public interface EventService {
  ResponseEntity<ClientResponseModel> emitNewlyCreatedBlocks(boolean fullTransactionObjects);
}
