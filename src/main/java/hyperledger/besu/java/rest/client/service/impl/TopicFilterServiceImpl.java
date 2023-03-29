package hyperledger.besu.java.rest.client.service.impl;

import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.service.TopicFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;

@Service
// This service is used for Topic filter related tasks
public class TopicFilterServiceImpl implements TopicFilterService {
  private static final DefaultBlockParameter fromBlock =
      DefaultBlockParameterName.EARLIEST; // optional, params - defaults to latest for both
  private static final DefaultBlockParameter toBlock = DefaultBlockParameterName.LATEST;
  @Autowired private EthEventsProperties ethEventsProperties;

  @Override
  public EthFilter createEthFilterWithSmartContractAddressList() {
    return new EthFilter(fromBlock, toBlock, ethEventsProperties.getSmartContract().getAddresses());
  }
}
