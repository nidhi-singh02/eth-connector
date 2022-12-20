package hyperledger.besu.java.rest.client.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "besu")
@RefreshScope
public class EthProperties {

  // TODO: change this when https is supported
  private List<String> rpcEndpoints;
  private Wallet wallet;
  private Client client;
  private Integer gasPrice;
  private Integer gasLimit;

  @Data
  public static class Wallet {
    private String password;
    private String path;
  }

  @Data
  public static class Client {
    private Rest rest;

    @Data
    public static class Rest {
      private String apikey;
    }
  }
}
