package hyperledger.besu.java.rest.client.model.abi;

import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import hyperledger.besu.java.rest.client.exception.ErrorCode;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.AbiDefinition;

import java.util.ArrayList;
import java.util.List;

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

    List<TypeReference<Type>> types = new ArrayList<>();

    for(NamedType namedType : outputs) {
      String type = namedType.getType();
      try {
        types.add(TypeReference.makeTypeReference(type));
      } catch (ClassNotFoundException e) {
        throw new BesuTransactionException(
            ErrorCode.HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR,
            "class not found");
      }
    }

    return types;
  }
}
