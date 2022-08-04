package hyperledger.besu.java.client.ethconnecter.model;

import org.web3j.abi.datatypes.Type;

import java.util.List;
import java.util.Map;

public class TransactionResponseModel {

    public TransactionResponseModel(Map<String, String> transactionDetails, String smartContractResponse) {
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
