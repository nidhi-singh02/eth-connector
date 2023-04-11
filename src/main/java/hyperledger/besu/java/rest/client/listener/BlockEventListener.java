package hyperledger.besu.java.rest.client.listener;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.filters.BlockFilterHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "events.block", havingValue = "true")
public class BlockEventListener implements Runnable {
  /** Loads the eth-connector config. */
  @Autowired private EthConfig ethConfig;

  /** Loads the block filter service. */
  @Autowired private BlockFilterHandler blockFilterHandler;

  /** todo: Improve the logic to include all nodes from the network. */
  @Override
  public void run() {
    blockFilterHandler.receiveNewlyAddedBlocksOnly();
  }
}
