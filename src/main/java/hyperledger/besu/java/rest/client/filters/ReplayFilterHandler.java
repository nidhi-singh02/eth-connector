package hyperledger.besu.java.rest.client.filters;

import hyperledger.besu.java.rest.client.config.EthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameter;

@Service
@Slf4j
// This class is for the future scope, this is currently not attached to the event listeners
public class ReplayFilterHandler {
  @Autowired private EthConfig ethConfig;

  // replay all blocks up to the most current, and provide notification (via the submitted Flowable)
  // once you've caught up:
  @Async
  public void replayPastBlocksFlowable(
      DefaultBlockParameter startBlockNumber,
      DefaultBlockParameter endBlockNumber,
      boolean fullTxObjects,
      boolean ascending) {
    ethConfig
        .getWeb3jList()
        .get(0)
        .replayPastBlocksFlowable(startBlockNumber, endBlockNumber, fullTxObjects, ascending)
        .doOnError(
            error ->
                log.error(
                    "Error occurred while listening to replayPastBlocksFlowable events"
                        + error.getMessage()))
        .subscribe(block -> {});
  }

  // replay all blocks to the most current, then be notified of new subsequent blocks being created:
  @Async
  public void replayPastAndFutureBlocksFlowable(
      DefaultBlockParameter startBlockNumber, boolean fullTxObjects) {

    ethConfig
        .getWeb3jList()
        .get(0)
        .replayPastAndFutureBlocksFlowable(startBlockNumber, fullTxObjects)
        .doOnError(
            error ->
                log.error(
                    "Error occurred while listening to replayPastAndFutureBlocks events"
                        + error.getMessage()))
        .subscribe(block -> {});
  }
}
