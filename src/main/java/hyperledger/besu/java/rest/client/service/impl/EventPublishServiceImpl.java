package hyperledger.besu.java.rest.client.service.impl;

import hyperledger.besu.java.rest.client.service.EventPublishService;
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

@Slf4j
@Service("eventPublishService")
@ConditionalOnProperty("kafka.event-listener.topic")
public class EventPublishServiceImpl implements EventPublishService {

  private static final String ERROR_MSG = "error-msg";
  private static final String CONTRACT_ADDRESS = "contract-address";
  private static final String FUNCTION_NAME = "function-name";
  public static final String EVENT_TYPE = "event-type";
  public static final String EVENT_TYPE_SC = "chaincode-event";
  public static final String EVENT_TYPE_BLOCK = "block-event";
  public static final String EVENT_TYPE_ERROR = "error-event";

  @Value("${kafka.event-listener.topic}")
  private String topicName;

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

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
}
