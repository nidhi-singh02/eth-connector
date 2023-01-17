package hyperledger.besu.java.rest.client.config;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.surefire.shade.org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * The type Kafka properties is added for fetching Kafka properties as configuration and can be used
 * in Consumer and Producer using @Autowired
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
@RefreshScope
public class KafkaProperties {

  static final String KAFKA_SECURITY_PROTOCOL_KEY = "security.protocol";
  static final String KAFKA_SECURITY_PROTOCOL_VALUE = "SASL_SSL";
  static final String KAFKA_SASL_MECHANISM_KEY = "sasl.mechanism";
  static final String KAFKA_SASL_MECHANISM_VALUE = "PLAIN";
  static final String KAFKA_SASL_JASS_ENDPOINT_KEY = "sasl.jaas.config";
  static final Integer KAFKA_INTG_MAX_POLL_INTERVAL = 500000;
  static final Integer KAFKA_INTG_MAX_POLL_RECORDS = 100;
  static final Integer KAFKA_INTG_SESSION_TIMEOUT = 30000;

  private List<Consumer> integrationPoints;
  private Producer eventListener;

  @Getter
  @Setter
  public static class Producer extends SSLProperties {
    private String brokerHost;
    private String topic;
    private String saslJaasConfig;

    @Override
    public String toString() {
      return "Producer{"
          + "brokerHost='"
          + brokerHost
          + '\''
          + ", topic='"
          + topic
          + '\''
          + ", saslJaasConfig='"
          + saslJaasConfig
          + '\''
          + '}';
    }
  }

  /** The type Ssl properties is added for configuring SSL configuration for Kafka Cluster. */
  @Getter
  @Setter
  public static class SSLProperties {

    protected boolean sslEnabled;
    protected String securityProtocol;
    protected String sslKeystoreLocation;
    protected String sslKeystorePassword;
    protected String sslTruststoreLocation;
    protected String sslTruststorePassword;
    protected String sslKeyPassword;
    protected String sslKeystoreBase64;
    protected String sslTruststoreBase64;

    protected boolean isSslAuthRequired() {
      boolean isProtocolSSL =
          StringUtils.isNotBlank(securityProtocol) && "ssl".equalsIgnoreCase(securityProtocol);
      return isProtocolSSL && sslEnabled;
    }
  }

  @Getter
  @Setter
  public static class Consumer extends SSLProperties {
    private String brokerHost;
    private String groupId;
    private String topic;
    private String saslJaasConfig;

    @Override
    public String toString() {
      return "Consumer{"
          + "brokerHost='"
          + brokerHost
          + '\''
          + ", groupId='"
          + groupId
          + '\''
          + ", topic='"
          + topic
          + '\''
          + ", saslJaasConfig='"
          + saslJaasConfig
          + '\''
          + '}';
    }
  }
}
