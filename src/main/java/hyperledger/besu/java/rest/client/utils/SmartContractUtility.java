package hyperledger.besu.java.rest.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.exception.ErrorCode;
import hyperledger.besu.java.rest.client.exception.FileParseException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.maven.surefire.shade.org.apache.commons.io.IOUtils;
import org.springframework.util.ResourceUtils;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.datatypes.AbiTypes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.AbiDefinition;

@UtilityClass
@Slf4j
public class SmartContractUtility {

  /** used to specify abi events from the definition. */
  private static final String ABI_TYPE_EVENT = "event";

  /** used to serialize and deserialize the definitions. */
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /** used to remove first two chars of hex string. */
  private static final int DATA_STRING_HEX_FORMAT_START_IDX = 2;

  /**
   * @param hexStr
   * @return the data string
   * @throws DecoderException
   * @throws UnsupportedEncodingException
   */
  public String convertToStringFromHex(final String hexStr)
      throws DecoderException, UnsupportedEncodingException {
    byte[] bytes = Hex.decodeHex(hexStr.substring(2).toCharArray());
    return new String(bytes, "UTF-8");
  }

  /**
   * @param smrtContractObj
   * @return the hashmap of events and hash
   */
  public Map<String, String> getEventHashFromAbi(
      final EthEventsProperties.SmartContract smrtContractObj) {
    Map<String, String> eventNameByHash = new HashMap<>();
    if (Objects.nonNull(smrtContractObj)
        && ObjectUtils.allNotNull(
            smrtContractObj.getAbiBasePath(), smrtContractObj.getAddresses())) {

      List<String> abiList = getAbiPathListForSmartContract(smrtContractObj);
      abiList.stream()
          .forEach(
              abiPathSmrtContractAddr -> {
                try {
                  addEventNameByHash(abiPathSmrtContractAddr, eventNameByHash);
                } catch (UnsupportedEncodingException e) {
                  log.error("Error while reading file {}", e.getMessage());
                }
              });
      return eventNameByHash;
    }
    log.info("Data related to SmartContract Event is missing");
    return null;
  }

  /**
   * @param type
   * @param indexed
   * @return type references for the classes
   */
  private static org.web3j.abi.TypeReference<? extends Type> getEvtFuncRef(
      final String type, final boolean indexed) {
    Class clazz = AbiTypes.getType(type);
    return org.web3j.abi.TypeReference.create(clazz, indexed);
  }

  /**
   * @param smartContract
   * @return the abi file list
   */
  private static List<String> getAbiPathListForSmartContract(
      final EthEventsProperties.SmartContract smartContract) {
    List<String> abiFileList = new ArrayList<>();
    String baseAbiPath = smartContract.getAbiBasePath();
    smartContract.getAddresses().stream()
        .forEach(
            abiPathSmartContractAddress ->
                getAbiFileListForSmartContracts(
                    baseAbiPath, abiPathSmartContractAddress, abiFileList));
    return abiFileList;
  }

  /**
   * @param aboBasePath
   * @param parentFolderNameForSCAddress
   * @param abiPathFileList
   */
  private void getAbiFileListForSmartContracts(
      final String aboBasePath,
      final String parentFolderNameForSCAddress,
      final List<String> abiPathFileList) {
    String addressPath = aboBasePath + parentFolderNameForSCAddress;
    try {
      File[] abiFiles = new File(addressPath).listFiles();
      Arrays.stream(abiFiles)
          .forEach(
              filePath -> {
                abiPathFileList.add(addressPath + "/" + filePath.getName());
              });
    } catch (Exception e) {
      log.error(ErrorCode.FILE_PARSE_ERROR.getReason(), e.getMessage());
    }
  }

  /**
   * @param abiPath
   * @param eventNameHexMapping
   */
  private void addEventNameByHash(
      final String abiPath, final Map<String, String> eventNameHexMapping)
      throws UnsupportedEncodingException {
    byte[] abiFileDefinition;
    try {
      abiFileDefinition = IOUtils.toByteArray(ResourceUtils.getURL(abiPath));
    } catch (IOException e) {
      throw new FileParseException(ErrorCode.FILE_PARSE_ERROR, e.getMessage());
    }
    String abiDefinitionForSmartContractAddressStringified =
        new String(abiFileDefinition, StandardCharsets.UTF_8);
    try {
      OBJECT_MAPPER
          .readValue(
              abiDefinitionForSmartContractAddressStringified,
              new TypeReference<List<AbiDefinition>>() { })
          .stream()
          .forEach(
              abiDefinition -> {
                createEventDefinition(abiDefinition, eventNameHexMapping);
              });

    } catch (JsonProcessingException e) {
      log.error("Unable to read Abi file", e.getMessage());
    }
  }

  /**
   * @param abiDef
   * @param evtByHash
   */
  private void createEventDefinition(
      final AbiDefinition abiDef, final Map<String, String> evtByHash) {
    List<org.web3j.abi.TypeReference<?>> parameters = new ArrayList<>();
    if (abiDef.getType().equals(ABI_TYPE_EVENT)) {
      abiDef.getInputs().stream()
          .forEach(
              abiDefinitionInputParameter -> {
                parameters.add(
                    getEvtFuncRef(
                        abiDefinitionInputParameter.getInternalType(),
                        abiDefinitionInputParameter.isIndexed()));
              });
      Event event = new Event(abiDef.getName(), parameters);
      String eventHash = EventEncoder.encode(event);
      evtByHash.put(eventHash, abiDef.getName());
    }
  }
}
