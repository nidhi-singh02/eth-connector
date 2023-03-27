package hyperledger.besu.java.rest.client.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import org.web3j.abi.datatypes.Type;

@Data
public class TransactionResponseModel implements Serializable {
  private List<TypedResponse> smartContractResponse;
  private TransactionDetails transactionDetails;

  @Builder
  @Data
  static class TypedResponse implements Serializable {
    private String type;
    private Object value;
  }

  @Builder
  @Data
  static class TransactionDetails implements Serializable {
    private String transactionHash;
    private BigInteger blockNumber;
  }

  public TransactionResponseModel(final List<Type> readValues) {
    // for each value that is read, convert to custom response
    smartContractResponse =
        readValues.stream()
            .map(
                value ->
                    TypedResponse.builder()
                        .type(value.getTypeAsString())
                        .value(value.getValue())
                        .build())
            .collect(Collectors.toList());
  }

  public TransactionResponseModel(
      final String transactionHash, final BigInteger blockNumber, final List<Type> readValues) {
    transactionDetails =
        TransactionDetails.builder()
            .transactionHash(transactionHash)
            .blockNumber(blockNumber)
            .build();
  }
}
