package hyperledger.besu.java.rest.client.filters;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.service.EventPublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlockFilterHandler {
  @Autowired private EthConfig ethConfig;

  @Autowired private EventPublishService eventPublishServiceImpl;

  // To receive all new blocks as they are added to the blockchain (the false parameter specifies
  // that we only want the blocks, not the embedded transactions too):
  @Async
  public void receiveNewlyAddedBlocksOnly() {
    ethConfig
        .getWeb3jList()
        .get(0)
        .blockFlowable(true)
        .doOnError(
            error ->
                log.error(
                    "Error occurred while listening to receiveNewlyAddedBlocksOnly events"
                        + error.getMessage()))
        .subscribe(
            block -> {
              if (!block.hasError()) {
                log.info("block event listening for block hash {}", block.getBlock().getHash());
                eventPublishServiceImpl.publishEventLogs(block);
              } else {
                log.error("Error in block {} " + block.getError());
              }
            });
  }
}
