package hyperledger.besu.java.rest.client.model.abi;

import static hyperledger.besu.java.rest.client.exception.ErrorCode.HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR;

import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.AbiTypes;
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

  public List<Type> getInputType(Object[] params) {
    List<NamedType> inputs = this.getInputs();
    if (CollectionUtils.isEmpty(inputs)) {
      return Collections.emptyList();
    }
    List<Type> inputTypes = new ArrayList<>();
    List<Class> inputClasses =
        inputs.stream()
            .map(AbiDefinition.NamedType::getType)
            .map(AbiTypes::getType)
            .collect(Collectors.toList());
    int index = 0;
    for (Class clazz : inputClasses) {
      Type type;
      try {
        type = (Type) clazz.getConstructor(params[index].getClass()).newInstance(params[index]);
        index++;
        inputTypes.add(type);
      } catch (InstantiationException
          | IllegalAccessException
          | InvocationTargetException
          | NoSuchMethodException e) {
        throw new BesuTransactionException(
            HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR,
            "Unable to retrieve input type from ABI Definition file",
            e);
      }
    }
    return inputTypes;
  }

  public List<TypeReference<?>> getOutputTypeReferences() {
    List<NamedType> outputs = this.getOutputs();
    if (CollectionUtils.isEmpty(outputs)) {
      return Collections.emptyList();
    }
    // for each output, generate a specific typed list
    List<TypeReference<?>> outputTypeReferences = new ArrayList<>();
    for (NamedType namedType : outputs) {
      Class clazz = AbiTypes.getType(namedType.getType());
      TypeReference typeReference = TypeReference.create(clazz);
      outputTypeReferences.add(typeReference);
    }
    if (outputTypeReferences.isEmpty()) {
      throw new BesuTransactionException(
          HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR,
          "Unable to retrieve output type from ABI Definition file");
    }
    return outputTypeReferences;
  }
}
