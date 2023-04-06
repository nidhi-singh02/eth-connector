package hyperledger.besu.java.rest.client.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyperledger.besu.java.rest.client.model.events.SCEventWrapper;
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

  /** error msg for kafka header key. */
  private static final String INCOMING_EVENT_LOG_ERROR_MSG = "error-msg";
  /** msg for kafka header key - contract-address. */
  private static final String SMART_CONTRACT_ADDRESS = "contract-address";

  /** msg for kafka header key - function-name. */
  private static final String SMARTCONTRACT_FUNCTION_NAME = "function-name";

  /** msg for kafka header key - event-type. */
  public static final String INCOMING_EVENT_LOGGING_TYPE = "event-type";

  /** msg for kafka header key - error-event. */
  public static final String INCOMING_EVENT_LOGGING_ERROR = "error-event";

  /** msg for kafka header key - log-event. */
  public static final String LOG_EVENT = "log-event";

  /** msg for kafka header key - eth-block-hash. */
  public static final String ETH_BLOCK_HASH = "eth-block-hash";

  /** msg for kafka header key - event-name. */
  public static final String INCOMING_EVENT_LOG_NAME = "event-name";

  /** kafka topic-name. */
  @Value("${kafka.event-listener.topic}")
  private String topicName;

  /** kafka template. */
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  /** objectMapper. */
  @Autowired private ObjectMapper objectMapper;

  /**
   * @param errorMsg contents of the error message
   * @param smartContractAddress chaincode name in the Fabric
   * @param functionName function name in a given chaincode.
   * @param parameters parameters sent to the chaincode
   * @return
   */
  @Override
  public boolean publishTransactionFailureEvent(
      final String errorMsg,
      final String smartContractAddress,
      final String functionName,
      final String parameters) {
    boolean status = true;

    try {
      ProducerRecord<String, String> producerRecord =
          new ProducerRecord<>(topicName, functionName, parameters);
      producerRecord
          .headers()
          .add(new RecordHeader(INCOMING_EVENT_LOG_ERROR_MSG,
              errorMsg.getBytes())
          );
      producerRecord
          .headers()
          .add(new RecordHeader(SMART_CONTRACT_ADDRESS,
              smartContractAddress.getBytes())
          );
      producerRecord
          .headers()
          .add(
              new RecordHeader(
                  INCOMING_EVENT_LOGGING_TYPE,
                  INCOMING_EVENT_LOGGING_ERROR.getBytes())
          );
      producerRecord
          .headers()
          .add(new RecordHeader(SMARTCONTRACT_FUNCTION_NAME,
              functionName.getBytes())
          );

      ListenableFuture<SendResult<String, String>> sendResultListenableFuture =
          kafkaTemplate.send(producerRecord);

      sendResultListenableFuture.addCallback(
          new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(final SendResult<String, String> result) {
              log.info(
                  "Sent message=["
                      + parameters
                      + "] with offset=["
                      + result.getRecordMetadata().offset()
                      + "]");
            }

            @Override
            public void onFailure(final Throwable ex) {
              log.error(
                  "Unable to send message=[{}] due to ERROR encountered: {}",
                  parameters,
                  ex.getMessage());
            }
          });

    } catch (Exception ex) {
      status = false;
      log.error("Error sending message - {}", ex.getMessage());
    }

    return status;
  }

  /**
   * @param incomingLog
   * @return true or false value
   */
  @Override
  public boolean publishEventLogs(final Object incomingLog) {
    boolean status = true;
    ProducerRecord<String, String> producerRecord;
    try {
      if (incomingLog instanceof SCEventWrapper) {

        producerRecord =
            new ProducerRecord<>(
                topicName,
                objectMapper.writeValueAsString(((SCEventWrapper) incomingLog)
                    .getLog())
            );
        producerRecord
            .headers()
            .add(
                new RecordHeader(
                    SMART_CONTRACT_ADDRESS,
                    ((SCEventWrapper) incomingLog).getLog().getAddress()
                        .getBytes()));
        producerRecord
            .headers()
            .add(
                new RecordHeader(
                    INCOMING_EVENT_LOG_NAME,
                    ((SCEventWrapper) incomingLog).getEventName().getBytes()));
      } else if (incomingLog instanceof EthBlock) {
        producerRecord =
            new ProducerRecord<>(topicName, objectMapper
                .writeValueAsString(incomingLog));
        producerRecord
            .headers()
            .add(
                new RecordHeader(
                    ETH_BLOCK_HASH,
                    ((EthBlock) incomingLog)
                        .getBlock()
                        .getHash()
                        .getBytes(StandardCharsets.UTF_8)));
      } else {
        throw new OperationsException("Error sending incoming event log to "
            + "topic" + " {}" + incomingLog);
      }

      producerRecord
          .headers()
          .add(
              new RecordHeader(
                  INCOMING_EVENT_LOGGING_TYPE,
                  LOG_EVENT.getBytes()));
      ListenableFuture<SendResult<String, String>> listenableFutureForSendResp =
          kafkaTemplate.send(producerRecord);

      listenableFutureForSendResp.addCallback(
          new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(final SendResult<String, String> result) {
              log.debug(
                  "Sent Log=["
                      + incomingLog
                      + "] with offset=["
                      + result.getRecordMetadata().offset()
                      + "]");
            }

            @Override
            public void onFailure(final Throwable ex) {
              log.error(
                  "Unable to send Log=[{}] due to ERROR encountered: {}",
                  incomingLog,
                  ex.getMessage());
            }
          });

    } catch (Exception ex) {
      status = false;
      log.error("Error sending message - {}", ex.getMessage());
    }

    return status;
  }
}
