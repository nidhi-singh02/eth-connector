package hyperledger.besu.java.rest.client.filters;

import hyperledger.besu.java.rest.client.config.EthConfig;
import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.exception.ErrorCode;
import hyperledger.besu.java.rest.client.exception.InvalidArgumentException;
import hyperledger.besu.java.rest.client.exception.OperationNotSupported;
import hyperledger.besu.java.rest.client.model.events.SCEventWrapper;
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
  /** Loads the eth-connector config. */
  @Autowired private EthConfig ethConfig;

  /** Loads the EventPublishService config. */
  @Autowired(required = false)
  private EventPublishService eventPublishServiceImpl;

  /** Loads the TopicFilterServiceImpl config. */
  @Autowired(required = false)
  private TopicFilterServiceImpl topicFilterService;

  /** Listens to the Event Logs using EthFilter. */
  @Async
  public void receiveAllEventsByEthFilter() {
    ethConfig
        .getWeb3jList()
        .get(0)
        .ethLogFlowable(topicFilterService.createEthFilterWithSCAddress())
        .doOnError(
            error -> {
              log.error("Error listening to logs " + error.getMessage());
            })
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

  /** @param eventLog */
  private void createAndSendSmartContractEvents(final Log eventLog) {
    eventLog.getTopics().stream()
        .forEach(
            topic -> {
              SCEventWrapper scEvent;
              try {
                scEvent = createSCEventObj(topic, eventLog);
              } catch (DecoderException e) {
                throw new InvalidArgumentException(
                    ErrorCode.INVALID_ARGUMENT_FOUND, e.getMessage());
              } catch (UnsupportedEncodingException unsupportedEncExe) {
                throw new OperationNotSupported(
                    ErrorCode.NOT_SUPPORTED, unsupportedEncExe.getMessage());
              }
              log.info("SC Event pushing to Kafka topic {}", scEvent);
              eventPublishServiceImpl.publishEventLogs(scEvent);
            });
  }

  /**
   * @param topic
   * @param eLog
   * @return the smart contract event wrapper object
   * @throws DecoderException
   * @throws UnsupportedEncodingException
   */
  private SCEventWrapper createSCEventObj(final String topic, final Log eLog)
      throws DecoderException, UnsupportedEncodingException {

    return SCEventWrapper.builder()
        .eventName(EthEventsProperties.getEventHasForSCAddress(topic))
        .data(SmartContractUtility.convertToStringFromHex(eLog.getData()))
        .log(eLog)
        .build();
  }
}
