package hyperledger.besu.java.rest.client.filters;

import hyperledger.besu.java.rest.client.config.EthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameter;

/**
 * This class is for the future scope, this is currently not
 *
 * <p>attached to the event listeners.
 */
@Service
@Slf4j
public class ReplayFilterHandler {

  /** Loads the eth-connector config. */
  @Autowired private EthConfig ethConfig;

  /**
   * @param startBlock
   * @param endBlock
   * @param fullTxObj
   * @param asc
   */
  @Async
  public void replayPastBlocksFlowable(
      final DefaultBlockParameter startBlock,
      final DefaultBlockParameter endBlock,
      final boolean fullTxObj,
      final boolean asc) {
    ethConfig
        .getWeb3jList()
        .get(0)
        .replayPastBlocksFlowable(startBlock, endBlock, fullTxObj, asc)
        .doOnError(
            error -> {
              log.error("Error listening to past block" + error.getMessage());
            })
        .subscribe(
            block -> {
              log.info("Listening to past block");
            });
  }

  /**
   * @param sBlk
   * @param txn
   */
  @Async
  public void replayPastAndFutureBlocksFlowable(
      final DefaultBlockParameter sBlk, final boolean txn) {

    ethConfig
        .getWeb3jList()
        .get(0)
        .replayPastAndFutureBlocksFlowable(sBlk, txn)
        .doOnError(
            e -> {
              log.error("Error in past and future events" + e.getMessage());
            })
        .subscribe(
            block -> {
              log.info("Listening to past-future block");
            });
  }
}
