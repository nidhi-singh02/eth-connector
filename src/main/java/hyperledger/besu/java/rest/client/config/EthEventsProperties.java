/**
 * This file is reading the Event Listening Configuration
 *
 * @since 1.0
 * @author kamini-kamal
 * @version 1.0
 */
package hyperledger.besu.java.rest.client.config;

import hyperledger.besu.java.rest.client.exception.ErrorCode;
import hyperledger.besu.java.rest.client.exception.NotFoundException;
import hyperledger.besu.java.rest.client.utils.SmartContractUtility;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "events")
@RefreshScope
@Slf4j
public class EthEventsProperties {
  /** This is used to enable block listening event. */
  private boolean block;
  /** This is used to capture smart-contract address and abiBasePath. */
  private SmartContract smartContract;

  /** Captures event name and hash generated from Abi definition. */
  private static Map<String, String> eventNameByHash = new HashMap<>();

  @Data
  public static class SmartContract {
    /** captures smart-contract address. */
    private List<String> addresses;
    /** captures abi file base path. */
    private String abiBasePath;
  }

  /** after the configuration is loaded, hash is generated for events. */
  @PostConstruct
  public void generateEventHashFromAbiFile() {
    eventNameByHash = SmartContractUtility.getEventHashFromAbi(smartContract);
    log.info("eventNameByHash {}", eventNameByHash);
  }

  /** @return */
  public static Map<String, String> getEventNameByHash() {
    return eventNameByHash;
  }

  /**
   * @param incomingEventHash
   * @return event name from hash
   */
  public static String getEventHasForSCAddress(final String incomingEventHash) {
    if (eventNameByHash.containsKey(incomingEventHash)) {
      return eventNameByHash.get(incomingEventHash);
    } else {
      throw new NotFoundException(ErrorCode.NO_EVENTS_FOUND, incomingEventHash);
    }
  }
}
