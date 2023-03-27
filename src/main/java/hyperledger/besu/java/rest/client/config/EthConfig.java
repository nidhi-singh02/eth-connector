package hyperledger.besu.java.rest.client.config;

import hyperledger.besu.java.rest.client.exception.ErrorCode;
import hyperledger.besu.java.rest.client.exception.ServiceException;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Data
@Slf4j
@Configuration
public class EthConfig {

  private List<Web3j> web3jList;
  @Autowired private EthProperties ethProperties;

//  @RefreshScope
  @Bean
  public Credentials getCredentials() {
    Credentials credentials;
    try {
      // read the file and load the wallet
      credentials =
          WalletUtils.loadCredentials(
              ethProperties.getWallet().getPassword(), ethProperties.getWallet().getPath());
    } catch (Exception e) {
      log.error("Error reading the wallet file: {}", e.getMessage());
      throw new ServiceException(ErrorCode.INITIALIZATION_FAILED, e.getCause().getMessage());
    }
    return credentials;
  }

  @PostConstruct
  public void getHttpService() {
    // TODO: supports non-https connections for now.
    List<HttpService> httpServices =
        ethProperties.getRpcEndpoints().stream().map(HttpService::new).collect(Collectors.toList());
    // create a connection pool instead of one node.
    // this helps in connecting to multiple nodes instead of one.
    // it is practical if one entity is managing multiple nodes.
    web3jList = httpServices.stream().map(Web3j::build).collect(Collectors.toList());
  }
}
