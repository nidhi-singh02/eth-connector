package hyperledger.besu.java.rest.client.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.web3j.abi.datatypes.Type;

public class TransactionResponseModel implements Serializable {

  public TransactionResponseModel(
      Map<String, String> transactionDetails, String smartContractResponse) {
    this.smartContractResponse = smartContractResponse;
    this.transactionDetails = transactionDetails;
    this.uint = null;
  }

  public TransactionResponseModel(Map<String, String> transactionDetails) {
    this.transactionDetails = transactionDetails;
    this.smartContractResponse = null;
    this.uint = null;
  }

  public TransactionResponseModel(List<Type> uint) {
    this.uint = uint;
    this.smartContractResponse = null;
    this.transactionDetails = null;
  }

  private Map<String, String> transactionDetails;
  private String smartContractResponse;
  private List<Type> uint;
}
