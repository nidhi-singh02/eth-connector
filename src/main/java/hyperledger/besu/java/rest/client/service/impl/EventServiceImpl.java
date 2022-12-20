package hyperledger.besu.java.rest.client.service.impl;

import hyperledger.besu.java.rest.client.exception.ErrorConstants;
import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import hyperledger.besu.java.rest.client.service.EventService;
import hyperledger.besu.java.rest.client.util.HelperModule;
import io.reactivex.Flowable;
import java.io.Serializable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.web3j.protocol.core.methods.response.EthBlock;

public class EventServiceImpl implements EventService {

  public ResponseEntity<ClientResponseModel> emitNewlyCreatedBlocks(
      boolean fullTransactionObjects) {
    Flowable<EthBlock> blockHashes = HelperModule.emitNewlyCreatedBlocks(fullTransactionObjects);
    return new ResponseEntity<>(
        new ClientResponseModel(ErrorConstants.NO_ERROR, (Serializable) blockHashes),
        HttpStatus.OK);
  }
}
