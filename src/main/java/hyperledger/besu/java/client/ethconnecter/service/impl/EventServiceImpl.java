package hyperledger.besu.java.client.ethconnecter.service.impl;

import hyperledger.besu.java.client.ethconnecter.exception.ErrorConstants;
import hyperledger.besu.java.client.ethconnecter.model.ClientResponseModel;
import hyperledger.besu.java.client.ethconnecter.service.EventService;
import hyperledger.besu.java.client.ethconnecter.util.HelperModule;
import io.reactivex.Flowable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.Serializable;

public class EventServiceImpl implements EventService {

    public ResponseEntity<ClientResponseModel> emitNewlyCreatedBlocks(boolean fullTransactionObjects){
        Flowable<EthBlock> blockHashes = HelperModule.emitNewlyCreatedBlocks(fullTransactionObjects);
        return new ResponseEntity<>(
                new ClientResponseModel(ErrorConstants.NO_ERROR, (Serializable) blockHashes), HttpStatus.OK);
    }
}
