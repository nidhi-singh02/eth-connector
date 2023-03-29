package hyperledger.besu.java.rest.client.config;

import hyperledger.besu.java.rest.client.exception.ErrorCode;
import hyperledger.besu.java.rest.client.exception.NotFoundException;
import hyperledger.besu.java.rest.client.utils.SmartContractUtility;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "events")
@RefreshScope
public class EthEventsProperties {
  private boolean block;
  private SmartContract smartContract;
  private static Map<String, String> eventNameByHash = new HashMap<>();

  @Data
  public static class SmartContract {
    private List<String> addresses;
    private String abiBasePath;
  }

  @PostConstruct
  public void generateEventHashFromAbiFile() {
    eventNameByHash = SmartContractUtility.generateEventHashFromAbiFiles(smartContract);
  }

  public static String getEventHasStringForSmartContractAddress(String incomingEventHash) {
    if (eventNameByHash.containsKey(incomingEventHash)) {
      return eventNameByHash.get(incomingEventHash);
    } else {
      throw new NotFoundException(
          ErrorCode.NO_EVENTS_FOUND, "No Events found for hash " + incomingEventHash);
    }
  }
}
