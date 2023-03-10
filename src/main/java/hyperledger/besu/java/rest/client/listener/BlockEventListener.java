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

  @Autowired private EthConfig ethConfig;
  @Autowired private BlockFilterHandler blockFilterHandler;

  @Override
  public void run() {
    // TODO: Improve the logic to include all nodes from the network
    // get events with all transactions data
    blockFilterHandler.receiveNewlyAddedBlocksOnly();
  }
}
