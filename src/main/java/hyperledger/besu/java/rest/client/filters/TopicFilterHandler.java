package hyperledger.besu.java.rest.client.filters;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.service.EventPublishService;
import hyperledger.besu.java.rest.client.service.impl.TopicFilterServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TopicFilterHandler {
  @Autowired private EthConfig ethConfig;

  @Autowired(required = false)
  private EventPublishService eventPublishServiceImpl;

  @Autowired private TopicFilterServiceImpl topicFilterService;

  @Async
  public void receiveAllEventsByEthFilter() {
    ethConfig
        .getWeb3jList()
        .get(0)
        .ethLogFlowable(topicFilterService.createEthFilterWithSmartContractAddressList())
        .doOnError(
            error ->
                log.error("Error occurred while listening to log events " + error.getMessage()))
        .subscribe(
            logs -> {
              log.info("topic event listening {}", logs);
              eventPublishServiceImpl.publishEventLogs(logs);
            });
  }
}
