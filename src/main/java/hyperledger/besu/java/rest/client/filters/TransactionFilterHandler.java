package hyperledger.besu.java.rest.client.filters;

import hyperledger.besu.java.rest.client.config.EthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameter;

/**
 * This class is for the future scope,
 *
 * <p>this is currently not attached to
 *
 * <p>the event listeners.
 */
@Service
@Slf4j
public class TransactionFilterHandler {
  /** Loads the eth-connector config. */
  @Autowired private EthConfig ethConfig;

  /** To receive all new transactions as they are added to the blockchain. */
  @Async
  public void receiveNewTxn() {

    ethConfig
        .getWeb3jList()
        .get(0)
        .transactionFlowable()
        .doOnError(
            err -> {
              log.error("Error listening to new txn" + err.getMessage());
            })
        .subscribe(
            tx -> {
              log.info("Listening to new transactions events");
            });
  }

  /** To receive all pending transactions. */
  @Async
  public void receivePendingTxn() {

    ethConfig
        .getWeb3jList()
        .get(0)
        .pendingTransactionFlowable()
        .doOnError(
            err -> {
              log.error("Error listening to pending txn" + err.getMessage());
            })
        .subscribe(
            tx -> {
              log.info("Listening to pending transactions events");
            });
  }

  /** @param startBlock */
  @Async
  public void replayPastAndFutureTxn(final DefaultBlockParameter startBlock) {

    ethConfig
        .getWeb3jList()
        .get(0)
        .replayPastAndFutureTransactionsFlowable(startBlock)
        .doOnError(
            err -> {
              log.error("Error in past-future event" + err.getMessage());
            })
        .subscribe(
            tx -> {
              log.info("Listening to  past and future events");
            });
  }
}
