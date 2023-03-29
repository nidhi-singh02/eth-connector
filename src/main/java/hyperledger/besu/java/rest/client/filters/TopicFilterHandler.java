package hyperledger.besu.java.rest.client.filters;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.exception.ErrorCode;
import hyperledger.besu.java.rest.client.exception.InvalidArgumentException;
import hyperledger.besu.java.rest.client.exception.OperationNotSupported;
import hyperledger.besu.java.rest.client.model.events.SmartContractEventWrapper;
import hyperledger.besu.java.rest.client.service.EventPublishService;
import hyperledger.besu.java.rest.client.service.impl.TopicFilterServiceImpl;
import hyperledger.besu.java.rest.client.utils.SmartContractUtility;
import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;

@Service
@Slf4j
public class TopicFilterHandler {
  @Autowired private EthConfig ethConfig;

  @Autowired private EventPublishService eventPublishServiceImpl;

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
              try {
                createAndSendSmartContractEvents(logs);
              } catch (Exception e) {
                log.warn("ex {}", e.getMessage());
              }
            });
  }

  private void createAndSendSmartContractEvents(Log eventLog) {
    eventLog.getTopics().stream()
        .forEach(
            topic -> {
              SmartContractEventWrapper smartContractEvent;
              try {
                smartContractEvent = createSmartContractEventObject(topic, eventLog);
              } catch (DecoderException e) {
                throw new InvalidArgumentException(
                    ErrorCode.INVALID_ARGUMENT_FOUND, e.getMessage());
              } catch (UnsupportedEncodingException e) {
                throw new OperationNotSupported(ErrorCode.NOT_SUPPORTED, e.getMessage());
              }
              log.info("Smart Contract Event pushing to Kafka topic {}", smartContractEvent);
              eventPublishServiceImpl.publishEventLogs(smartContractEvent);
            });
  }

  private SmartContractEventWrapper createSmartContractEventObject(String topic, Log eventLog)
      throws DecoderException, UnsupportedEncodingException {

    return SmartContractEventWrapper.builder()
        .eventName(EthEventsProperties.getEventHasStringForSmartContractAddress(topic))
        .data(SmartContractUtility.convertToStringFromHex(eventLog.getData()))
        .log(eventLog)
        .build();
  }
}
