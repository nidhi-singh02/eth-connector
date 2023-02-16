package hyperledger.besu.java.rest.client.utils;

import hyperledger.besu.java.rest.client.model.abi.AbiDefinitionWrapper;
import java.util.List;
import java.util.Optional;
import org.web3j.protocol.core.methods.response.AbiDefinition;

public class AbiUtility {
  private static final String FUNCTION = "function";

  public static Optional<AbiDefinitionWrapper> getAbiDefinitionFromFunctionName(
      List<AbiDefinition> abiDefinitionList, String functionName) {
    // filter out based on the type function
    // the input function name should match the strong in the ABI definition
    // typecast the received ABI definition to a wrapper for operations
    return abiDefinitionList.stream()
        .filter(
            definition ->
                definition.getType().equals(FUNCTION) && definition.getName().equals(functionName))
        .map(AbiDefinitionWrapper::new)
        .findFirst();
  }
}
