package hyperledger.besu.java.rest.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

/** Bootstrap for the Eth-Connector application. */
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync
public class EthConnectorApplication extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(EthConnectorApplication.class, args);
  }

  /**
   * Configure spring application builder.
   *
   * @param builder the builder
   * @return the spring application builder
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    log.info("Servlet Initializer");
    return builder.sources(EthConnectorApplication.class);
  }
}
