package hyperledger.besu.java.rest.client.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyperledger.besu.java.rest.client.dto.MultipartABIFile;
import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import hyperledger.besu.java.rest.client.service.EventPublishService;
import hyperledger.besu.java.rest.client.service.TransactionService;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class has the consumer logic for processing
 *
 * <p>and adding transaction to fabric.
 */
@Slf4j
@Service
public class TransactionConsumer {

  /** Abi definition list to filter out header key. */
  private static final String ABI_DEFINITION_LIST = "abi-definition-list";

  /** smart-contract address to filter out header key. */
  private static final String CONTRACT_ADDRESS = "contract-address";

  /** function-name to filter out header key. */
  private static final String FUNCTION_NAME = "function-name";

  /** UTF_8 charset. */
  private static final Charset UTF_8 = StandardCharsets.UTF_8;

  /** object mapper to serialize and deserialize value in the correct format. */
  @Autowired private ObjectMapper objectMapper;

  /** transaction service required to perform operation on the besu network. */
  @Autowired private TransactionService transactionService;

  /** EventPublishService to manage incoming-events. */
  @Autowired(required = false)
  private EventPublishService eventPublishServiceImpl;

  /**
   * This method routes the kafka messages to appropriate
   *
   * <p>methods and acknowledges once processing
   *
   * <p>is complete.
   *
   * @param consumerRecordPayload ConsumerRecord payload from upstream system
   * @param acknowledgment Acknowledgment manual commit offset
   */
  public void listen(
      final ConsumerRecord<String, String> consumerRecordPayload,
      final Acknowledgment acknowledgment) {
    log.info(
        "Incoming Message details : Topic : "
            + consumerRecordPayload.topic()
            + ", partition : "
            + consumerRecordPayload.partition()
            + " , offset : "
            + consumerRecordPayload.offset()
            + " , message :"
            + consumerRecordPayload.value());

    Header[] kafkaHeaders = consumerRecordPayload.headers().toArray();

    MultipartFile abiDefinitionFile = null;
    String smartContractAddress = "";
    String transactionFunctionName = "";
    String transactionParameters = "";

    try {
      if (!consumerRecordPayload.value().isEmpty()) {
        transactionParameters = consumerRecordPayload.value();
      }

      for (Header msgHeader : kafkaHeaders) {
        log.info(
            "Header-Key : "
                + msgHeader.key()
                + " Header-Value: "
                + new String(msgHeader.value(), UTF_8));
        switch (msgHeader.key()) {
          case CONTRACT_ADDRESS:
            smartContractAddress = new String(msgHeader.value(), UTF_8);
            break;
          case FUNCTION_NAME:
            transactionFunctionName = new String(msgHeader.value(), UTF_8);
            break;
          case ABI_DEFINITION_LIST:
            abiDefinitionFile = new MultipartABIFile(msgHeader.value());
            break;
          default:
            break;
        }
      }

      if (!smartContractAddress.isEmpty()
          && !transactionFunctionName.isEmpty()
          && !transactionParameters.isEmpty()) {
        transactionService.execute(
            abiDefinitionFile,
            smartContractAddress,
            transactionFunctionName,
            transactionParameters);
      }
      acknowledgment.acknowledge();
    } catch (BesuTransactionException exception) {
      acknowledgment.acknowledge();
      eventPublishServiceImpl.publishTransactionFailureEvent(
          exception.getMessage(),
          smartContractAddress,
          transactionFunctionName,
          transactionParameters);
      log.error("Error in Submitting Transaction - {}", exception.getMessage());
    } catch (Exception e) {
      log.error("Error in Kafka Listener - Message Format {}", e.getMessage());
      e.printStackTrace();
    }
  }
}
