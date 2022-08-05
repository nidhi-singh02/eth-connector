package hyperledger.besu.java.client.ethconnecter.controller;

import hyperledger.besu.java.client.ethconnecter.model.ClientResponseModel;
import hyperledger.besu.java.client.ethconnecter.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

public class EventController {
    @Autowired
    EventService eventService;

    // The Rest endpoint for deploying smart contract
    @PostMapping("/emitNewlyCreatedBlocks")
    public ResponseEntity<ClientResponseModel> emitNewlyCreatedBlocks() {
        return eventService.emitNewlyCreatedBlocks(true);
    }
}
