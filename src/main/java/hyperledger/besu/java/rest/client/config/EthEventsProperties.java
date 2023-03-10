package hyperledger.besu.java.rest.client.config;

import java.util.List;
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
  private List<String> smartContract;
}
