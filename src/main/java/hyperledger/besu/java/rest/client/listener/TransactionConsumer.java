package hyperledger.besu.java.rest.client.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import hyperledger.besu.java.rest.client.service.EventPublishService;
import hyperledger.besu.java.rest.client.service.TransactionService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.AbiDefinition;

/*
 * This class has the consumer logic for processing and adding transaction to fabric
 */
@Slf4j
@Service
public class TransactionConsumer {

  private static final String ABI_DEFINITION_LIST = "abi-definition-list";
  private static final String CONTRACT_ADDRESS = "contract-address";
  private static final String FUNCTION_NAME = "function-name";

  @Autowired private ObjectMapper objectMapper;

  @Autowired private TransactionService transactionService;

  @Autowired(required = false)
  private EventPublishService eventPublishServiceImpl;

  /**
   * This method routes the kafka messages to appropriate methods and acknowledges once processing
   * is complete
   *
   * @param message ConsumerRecord payload from upstream system
   * @param acknowledgment Acknowledgment manual commit offset
   */
  public void listen(ConsumerRecord<String, String> message, Acknowledgment acknowledgment) {
    log.info(
        "Incoming Message details : Topic : "
            + message.topic()
            + ", partition : "
            + message.partition()
            + " , offset : "
            + message.offset()
            + " , message :"
            + message.value());

    Header[] kafkaHeaders = message.headers().toArray();

    String abiDefinitionList = "";
    String contractAddress = "";
    String transactionFunctionName = "";
    String transactionParams = "";

    try {
      if (!message.value().isEmpty()) {
        transactionParams = message.value();
      }

      for (Header msgHeader : kafkaHeaders) {
        log.info(
            "Header-Key : "
                + msgHeader.key()
                + " Header-Value: "
                + new String(msgHeader.value(), StandardCharsets.UTF_8));
        switch (msgHeader.key()) {
          case CONTRACT_ADDRESS:
            contractAddress = new String(msgHeader.value(), StandardCharsets.UTF_8);
            break;
          case FUNCTION_NAME:
            transactionFunctionName = new String(msgHeader.value(), StandardCharsets.UTF_8);
            break;
          case ABI_DEFINITION_LIST:
            abiDefinitionList = new String(msgHeader.value(), StandardCharsets.UTF_8);
            break;
          default:
            break;
        }
      }

      // convert from string to the ABI definition list
      List<AbiDefinition> abiDefinitions =
          objectMapper.readValue(abiDefinitionList, new TypeReference<List<AbiDefinition>>() {});

      if (!contractAddress.isEmpty()
          && !transactionFunctionName.isEmpty()
          && !transactionParams.isEmpty()) {
        transactionService.execute(
            abiDefinitions, contractAddress, transactionFunctionName, transactionParams);
      }
      acknowledgment.acknowledge();
    } catch (BesuTransactionException e) {
      acknowledgment.acknowledge();
      eventPublishServiceImpl.publishTransactionFailureEvent(
          e.getMessage(), contractAddress, transactionFunctionName, transactionParams);
      log.error("Error in Submitting Transaction - Exception - {}", e.getMessage());
    } catch (Exception ex) {
      log.error("Error in Kafka Listener - Message Format exception - {}", ex.getMessage());
      ex.printStackTrace();
    }
  }
}
