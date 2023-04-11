package hyperledger.besu.java.rest.client.listener;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.filters.TopicFilterHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartContractEventListener implements Runnable {
  /** Loads the eth-connector config. */
  @Autowired private EthConfig ethConfig;

  /** Loads the topic-filter service. */
  @Autowired private TopicFilterHandler topicFilterHandler;

  /** Loads the event-properties config. */
  @Autowired(required = false)
  private EthEventsProperties ethEventsProperties;

  /**
   * This run method will be called by AsyncEventListener
   *
   * <p>after successful Bean creation.
   */
  @Override
  public void run() {
    if (ObjectUtils.isNotEmpty(EthEventsProperties.getEventNameByHash())) {
      topicFilterHandler.receiveAllEventsByEthFilter();
    }
  }
}
