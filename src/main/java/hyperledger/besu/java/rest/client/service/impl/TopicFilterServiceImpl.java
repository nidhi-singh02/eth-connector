package hyperledger.besu.java.rest.client.service.impl;

import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.service.TopicFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;

/** This service is used for Topic filter related tasks. */
@Service
@ConditionalOnProperty(
    prefix = "events.smartContract",
    name = {"addresses", "abiBasePath"},
    matchIfMissing = true)
public class TopicFilterServiceImpl implements TopicFilterService {

  /** used when the event configs are present. */
  @Autowired(required = false)
  private EthEventsProperties ethEvtProp;

  /**
   * used for creating eth filter for sc-addresses, from and to block.
   *
   * @return the ethFilter object
   */
  @Override
  public EthFilter createEthFilterWithSCAddress() {
    return new EthFilter(
        DefaultBlockParameterName.EARLIEST,
        DefaultBlockParameterName.LATEST,
        ethEvtProp.getSmartContract().getAddresses());
  }
}
