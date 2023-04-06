package hyperledger.besu.java.rest.client.filters;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.service.EventPublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "events", name = "block", havingValue = "true")
public class BlockFilterHandler {
  /** Loads the eth-connector config. */
  @Autowired private EthConfig ethConfig;

  /** Loads the EventPublishService. */
  @Autowired(required = false)
  private EventPublishService eventPublishServiceImpl;

  /**
   * To receive all new blocks as they are added to the blockchain
   *
   * <p>(the false parameter specifies
   *
   * <p>that we only want the blocks, not the embedded transactions too).
   */
  @Async
  public void receiveNewlyAddedBlocksOnly() {
    ethConfig
        .getWeb3jList()
        .get(0)
        .blockFlowable(true)
        .doOnError(
            error -> {
              log.error("Error listening new block" + error.getMessage());
            })
        .subscribe(
            block -> {
              if (!block.hasError()) {
                log.info("listening for block {}", block.getBlock().getHash());
                eventPublishServiceImpl.publishEventLogs(block);
              } else {
                log.error("Error in block {} ", block.getError());
              }
            });
  }
}
