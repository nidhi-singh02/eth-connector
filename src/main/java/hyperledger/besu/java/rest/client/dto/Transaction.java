package hyperledger.besu.java.rest.client.dto;

import static hyperledger.besu.java.rest.client.exception.ErrorCode.HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR;

import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import hyperledger.besu.java.rest.client.model.abi.AbiDefinitionWrapper;
import hyperledger.besu.java.rest.client.utils.AbiUtility;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.response.AbiDefinition;

@Slf4j
@Data
@Builder
public class Transaction {

  private List<AbiDefinition> abiDefinitionList;
  private String functionName;
  private Object[] params;
  private String contractAddress;
  private Function functionRef;
  private String compiledHexBinary;
  private BigInteger gasPrice;
  private BigInteger gasLimit;
  private BigInteger nonce;

  private Optional<Function> getFunction() {
    // This method converts the raw input of function name and parameters into
    // the object Function that gets consumed by the library.
    Optional<AbiDefinitionWrapper> abiDefinitionOptional =
        AbiUtility.getAbiDefinitionFromFunctionName(abiDefinitionList, functionName);
    if (!abiDefinitionOptional.isPresent()) {
      throw new BesuTransactionException(
          HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR,
          "Unable to find the ABI definition, cannot read output");
    }
    AbiDefinitionWrapper abiDefinition = abiDefinitionOptional.get();
    List<Type> inputParams = abiDefinition.getInputType(params);
    List<TypeReference<?>> outputParams = abiDefinition.getOutputTypeReferences();
    try {
      this.functionRef = new Function(functionName, inputParams, outputParams);
    } catch (Exception ex) {
      // handle exceptions
    }
    return Optional.ofNullable(functionRef);
    // Collections.singletonList(new Uint(BigInteger.valueOf(0)))
  }

  public String getEncodedFunction() {
    Optional<Function> function = getFunction();
    // TODO: Error if function is empty
    return FunctionEncoder.encode(function.get());
  }

  public List<Type> getDecodedFunction(String response) {
    Optional<Function> function = getFunction();
    return FunctionReturnDecoder.decode(response, function.get().getOutputParameters());
  }

  public List<TypeReference<Type>> getOutputParams() {
    return this.getFunctionRef().getOutputParameters();
  }

  public RawTransaction getRawTransaction() {
    // Construct the payload for a transaction
    // get the gas price and gas limit from the configuration
    // i.e. A transaction object created out of input configuration
    return RawTransaction.createTransaction(
        nonce, this.gasPrice, this.gasLimit, contractAddress, getEncodedFunction());
  }

  public RawTransaction getContractTransaction() {
    // create contract transaction
    return RawTransaction.createContractTransaction(
        nonce,
        this.gasPrice,
        this.gasLimit,
        BigInteger.ZERO, // gas price needed in the smart contract
        this.compiledHexBinary); // binary is
  }
}
