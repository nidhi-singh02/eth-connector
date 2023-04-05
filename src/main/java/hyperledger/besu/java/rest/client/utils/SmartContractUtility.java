package hyperledger.besu.java.rest.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.exception.ErrorCode;
import hyperledger.besu.java.rest.client.exception.FileParseException;
import hyperledger.besu.java.rest.client.exception.InvalidArgumentException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.maven.surefire.shade.org.apache.commons.io.IOUtils;
import org.springframework.util.ResourceUtils;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.AbiTypes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.AbiDefinition;

@UtilityClass
public class SmartContractUtility {

  private static final String ABI_TYPE_EVENT = "event";
  private static final ObjectMapper mapper = new ObjectMapper();

  public String convertToStringFromHex(String hexString)
      throws DecoderException, UnsupportedEncodingException {
    byte[] bytes = Hex.decodeHex(hexString.substring(2).toCharArray());
    return new String(bytes, "UTF-8");
  }

  public Map<String, String> generateEventHashFromAbiFiles(
      EthEventsProperties.SmartContract smartContract) {
    Map<String, String> eventNameByHash = new HashMap<>();
    List<String> abiFileList = getAbiFilePathListForSmartContract(smartContract);
    abiFileList.stream().forEach(abiFilePath -> addEventNameByHash(abiFilePath, eventNameByHash));
    return eventNameByHash;
  }

  private static TypeReference<? extends Type> getEventFuncParameters(
      String type, boolean indexed) {
    Class clazz = AbiTypes.getType(type);
    return TypeReference.create(clazz, indexed);
  }

  private static List<String> getAbiFilePathListForSmartContract(
      EthEventsProperties.SmartContract smartContract) {
    List<String> abiFileList = new ArrayList<>();
    String baseAbiPath = smartContract.getAbiBasePath();
    smartContract.getAddresses().stream()
        .forEach(address -> getAbiListForSmartContracts(baseAbiPath, address, abiFileList));
    return abiFileList;
  }

  private void getAbiListForSmartContracts(
      String baseAbiPath, String parentFolderName, List<String> abiFileList) {
    String smartContractPath = baseAbiPath + parentFolderName;
    File[] abiFiles = new File(smartContractPath).listFiles();
    Arrays.stream(abiFiles)
        .forEach(file -> abiFileList.add(smartContractPath + "/" + file.getName()));
  }

  private void addEventNameByHash(String abiFilePath, Map<String, String> eventNameByHash) {
    byte[] expectedTemplate;
    try {
      expectedTemplate = IOUtils.toByteArray(ResourceUtils.getURL(abiFilePath));
    } catch (IOException e) {
      throw new FileParseException(ErrorCode.FILE_PARSE_ERROR, e.getMessage());
    }
    String abiString = new String(expectedTemplate, StandardCharsets.UTF_8);
    try {
      mapper
          .readValue(
              abiString,
              new com.fasterxml.jackson.core.type.TypeReference<List<AbiDefinition>>() {})
          .stream()
          .forEach(abiDef -> createEventDefinition(abiDef, eventNameByHash));

    } catch (JsonProcessingException e) {
      throw new InvalidArgumentException(ErrorCode.INVALID_ARGUMENT_FOUND, e.getMessage());
    }
  }

  private void createEventDefinition(AbiDefinition abiDef, Map<String, String> eventNameByHash) {
    List<org.web3j.abi.TypeReference<?>> parameters = new ArrayList<>();
    if (abiDef.getType().equals(ABI_TYPE_EVENT)) {
      abiDef.getInputs().stream()
          .forEach(
              input ->
                  parameters.add(
                      getEventFuncParameters(input.getInternalType(), input.isIndexed())));
      Event event = new Event(abiDef.getName(), parameters);
      String eventHash = EventEncoder.encode(event);
      eventNameByHash.put(eventHash, abiDef.getName());
    }
  }
}
