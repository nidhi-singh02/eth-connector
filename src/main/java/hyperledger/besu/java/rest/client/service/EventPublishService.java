package hyperledger.besu.java.rest.client.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * The EventPublishService is a service class, which include the kafka template. It sends the
 * Message to the the Event Kafka message topic
 */
@ConditionalOnProperty("kafka.event-listener.brokerHost")
public interface EventPublishService {
  /**
   * @param errorMsg contents of the error message
   * @param contractName chaincode name in the Fabric
   * @param functionName function name in a given chaincode.
   * @param parameters parameters sent to the chaincode
   * @return status of the published message to Kafka, successful or not
   */
  boolean publishTransactionFailureEvent(
      String errorMsg, String contractName, String functionName, String parameters);
}
