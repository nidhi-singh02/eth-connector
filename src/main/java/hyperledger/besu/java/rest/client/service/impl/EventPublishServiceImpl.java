package hyperledger.besu.java.rest.client.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyperledger.besu.java.rest.client.model.events.SmartContractEventWrapper;
import hyperledger.besu.java.rest.client.service.EventPublishService;
import java.nio.charset.StandardCharsets;
import javax.management.OperationsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.web3j.protocol.core.methods.response.EthBlock;

@Slf4j
@Service("eventPublishService")
@ConditionalOnProperty("kafka.event-listener.topic")
public class EventPublishServiceImpl implements EventPublishService {

  private static final String ERROR_MSG = "error-msg";
  private static final String CONTRACT_ADDRESS = "contract-address";
  private static final String FUNCTION_NAME = "function-name";
  public static final String EVENT_TYPE = "event-type";
  public static final String EVENT_TYPE_ERROR = "error-event";
  public static final String EVENT_TYPE_LOG = "log-event";
  public static final String ETH_BLOCK_HASH = "eth-block-hash";
  public static final String EVENT_NAME = "event-name";

  @Value("${kafka.event-listener.topic}")
  private String topicName;

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired private ObjectMapper mapper;

  @Override
  public boolean publishTransactionFailureEvent(
      String errorMsg, String contractAddress, String functionName, String parameters) {
    boolean status = true;

    try {
      ProducerRecord<String, String> producerRecord =
          new ProducerRecord<>(topicName, functionName, parameters);
      producerRecord.headers().add(new RecordHeader(ERROR_MSG, errorMsg.getBytes()));
      producerRecord.headers().add(new RecordHeader(CONTRACT_ADDRESS, contractAddress.getBytes()));
      producerRecord.headers().add(new RecordHeader(EVENT_TYPE, EVENT_TYPE_ERROR.getBytes()));
      producerRecord.headers().add(new RecordHeader(FUNCTION_NAME, functionName.getBytes()));

      ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(producerRecord);

      future.addCallback(
          new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
              log.info(
                  "Sent message=["
                      + parameters
                      + "] with offset=["
                      + result.getRecordMetadata().offset()
                      + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
              log.error("Unable to send message=[{}] due to: {}", parameters, ex.getMessage());
            }
          });

    } catch (Exception ex) {
      status = false;
      log.error("Error sending message - {}", ex.getMessage());
    }

    return status;
  }

  @Override
  public boolean publishEventLogs(Object eventLog) {
    boolean status = true;
    ProducerRecord<String, String> producerRecord;
    try {
      if (eventLog instanceof SmartContractEventWrapper) {

        producerRecord =
            new ProducerRecord<>(
                topicName,
                mapper.writeValueAsString(((SmartContractEventWrapper) eventLog).getLog()));
        producerRecord
            .headers()
            .add(
                new RecordHeader(
                    CONTRACT_ADDRESS,
                    ((SmartContractEventWrapper) eventLog).getLog().getAddress().getBytes()));
        producerRecord
            .headers()
            .add(
                new RecordHeader(
                    EVENT_NAME, ((SmartContractEventWrapper) eventLog).getEventName().getBytes()));
      } else if (eventLog instanceof EthBlock) {
        producerRecord = new ProducerRecord<>(topicName, mapper.writeValueAsString(eventLog));
        producerRecord
            .headers()
            .add(
                new RecordHeader(
                    ETH_BLOCK_HASH,
                    ((EthBlock) eventLog).getBlock().getHash().getBytes(StandardCharsets.UTF_8)));
      } else {
        throw new OperationsException("Cannot send message for eventLog {}" + eventLog);
      }

      producerRecord.headers().add(new RecordHeader(EVENT_TYPE, EVENT_TYPE_LOG.getBytes()));
      ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(producerRecord);

      future.addCallback(
          new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
              log.debug(
                  "Sent Log=["
                      + eventLog
                      + "] with offset=["
                      + result.getRecordMetadata().offset()
                      + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
              log.error("Unable to send Log=[{}] due to: {}", eventLog, ex.getMessage());
            }
          });

    } catch (Exception ex) {
      status = false;
      log.error("Error sending message - {}", ex.getMessage());
    }

    return status;
  }
}
