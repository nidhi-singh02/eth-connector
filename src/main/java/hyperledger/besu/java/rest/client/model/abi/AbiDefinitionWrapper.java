package hyperledger.besu.java.rest.client.model.abi;

import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import hyperledger.besu.java.rest.client.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.AbiDefinition;

/**
 * The class wraps AbiDefinition with extended functionality to be consumed or used in this
 * repository.
 */
public class AbiDefinitionWrapper extends AbiDefinition {
  public AbiDefinitionWrapper(AbiDefinition from) {
    super(from);
  }

  public List<TypeReference<Type>> getOutputTypeReferences() {
    List<NamedType> outputs = this.getOutputs();
    // for each output, generate a specific typed list
    List<TypeReference<Type>> types =
        outputs.stream()
            .map(
                namedType -> {
                  // get the type information
                  String type = namedType.getType();
                  // check which type does it fit
                  try {
                    return TypeReference.makeTypeReference(type);
                  } catch (ClassNotFoundException e) {
                    // rethrow the exception
                    throw new BesuTransactionException(
                        ErrorCode.HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR,
                        "class not found");
                  }
                })
            .collect(Collectors.toList());
    return types;
  }
}
