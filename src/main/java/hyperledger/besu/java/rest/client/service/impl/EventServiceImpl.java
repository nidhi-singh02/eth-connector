package hyperledger.besu.java.rest.client.service.impl;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.core.DefaultBlockParameterNumber;

public class EventServiceImpl implements EventService {

  @Autowired private EthConfig ethConfig;

  @Override
  public void readBlock(int blockNumber) {
    ethConfig
        .getWeb3jList()
        .get(0)
        .replayPastBlocksFlowable(
            new DefaultBlockParameterNumber(blockNumber - 1),
            new DefaultBlockParameterNumber(blockNumber),
            true)
        .subscribe(
            ethBlock -> {
              // process the block
            });
  }
}
