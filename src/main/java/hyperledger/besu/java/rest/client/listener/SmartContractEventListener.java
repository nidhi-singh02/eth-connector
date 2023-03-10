package hyperledger.besu.java.rest.client.listener;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.exception.ErrorCode;
import hyperledger.besu.java.rest.client.exception.NotFoundException;
import hyperledger.besu.java.rest.client.filters.TopicFilterHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SmartContractEventListener implements Runnable {
  @Autowired private EthConfig ethConfig;
  @Autowired private TopicFilterHandler topicFilterHandler;
  @Autowired private EthEventsProperties ethEventsProperties;

  @Override
  public void run() {
    // only use smartContract address
    if (ObjectUtils.isEmpty(ethEventsProperties.getSmartContract())) {
      throw new NotFoundException(ErrorCode.NOT_FOUND, "Smart Contract Address list is empty");
    }
    topicFilterHandler.receiveAllEventsByEthFilter();
  }
}
