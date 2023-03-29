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
public class TransactionFilterHandler {
  @Autowired private EthConfig ethConfig;

  // To receive all new transactions as they are added to the blockchain:
  @Async
  public void receiveAllNewTransaction() {

    ethConfig
        .getWeb3jList()
        .get(0)
        .transactionFlowable()
        .doOnError(
            error ->
                log.error(
                    "Error occurred while listening to receiveAllNewTransaction events"
                        + error.getMessage()))
        .subscribe(tx -> {});
  }

  // To receive all pending transactions as they are submitted to the network (i.e. before they have
  // been grouped into a block together):
  @Async
  public void receiveAllPendingTransactions() {

    ethConfig
        .getWeb3jList()
        .get(0)
        .pendingTransactionFlowable()
        .doOnError(
            error ->
                log.error(
                    "Error occurred while listening to receiveAllPendingTransactions events"
                        + error.getMessage()))
        .subscribe(tx -> {});
  }

  // Replay all blocks to the most current, but with transactions contained within blocks:
  @Async
  public void replayPastAndFutureTransactions(DefaultBlockParameter startBlock) {

    ethConfig
        .getWeb3jList()
        .get(0)
        .replayPastAndFutureTransactionsFlowable(startBlock)
        .doOnError(
            error ->
                log.error(
                    "Error occurred while listening to replayPastAndFutureTransactions events"
                        + error.getMessage()))
        .subscribe(tx -> {});
  }
}
