package hyperledger.besu.java.rest.client.service;

import org.web3j.protocol.core.methods.request.EthFilter;

public interface TopicFilterService {
  EthFilter createEthFilterWithSmartContractAddressList();
}
