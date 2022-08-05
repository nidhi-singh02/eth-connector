package hyperledger.besu.java.client.ethconnecter.service;

import hyperledger.besu.java.client.ethconnecter.model.ClientResponseModel;
import org.springframework.http.ResponseEntity;
import org.web3j.protocol.core.DefaultBlockParameter;

public interface EventService {
    public ResponseEntity<ClientResponseModel> emitNewlyCreatedBlocks(boolean fullTransactionObjects);
}
