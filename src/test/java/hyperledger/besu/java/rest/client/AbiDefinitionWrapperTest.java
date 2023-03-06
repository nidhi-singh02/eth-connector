package hyperledger.besu.java.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyperledger.besu.java.rest.client.dto.Transaction;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.AbiDefinition;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AbiDefinitionWrapperTest {

  private List<AbiDefinition> abiDefinitionList1;
  private List<AbiDefinition> abiDefinitionList2;

  @BeforeAll
  void init() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    abiDefinitionList1 =
        mapper.readValue(
            new FileReader("src/test/resources/Example1.abi"),
            mapper.getTypeFactory().constructCollectionType(List.class, AbiDefinition.class));
    abiDefinitionList2 =
        mapper.readValue(
            new FileReader("src/test/resources/Example2.abi"),
            mapper.getTypeFactory().constructCollectionType(List.class, AbiDefinition.class));
  }

  @Test
  void testGetLatestPrice() {
    Transaction transaction =
        Transaction.builder()
            .functionName("getLatestPrice")
            .abiDefinitionList(abiDefinitionList1)
            .build();
    String result = transaction.getEncodedFunction();
    Function function =
        new Function(
            "getLatestPrice",
            Collections.emptyList(),
            Arrays.asList(new TypeReference<Int256>() {}));
    String expectedResult = FunctionEncoder.encode(function);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testTransfer() {

    Object[] params = {"0x12345", BigInteger.valueOf(1)};
    Transaction transaction =
        Transaction.builder()
            .functionName("transfer")
            .abiDefinitionList(abiDefinitionList2)
            .params(params)
            .build();
    String result = transaction.getEncodedFunction();
    Function function =
        new Function(
            "transfer",
            Arrays.asList(new Address((String) params[0]), new Uint256((BigInteger) params[1])),
            Collections.emptyList());
    String expectedResult = FunctionEncoder.encode(function);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testGetBalance() {

    Object[] params = {"0x12345"};
    Transaction transaction =
        Transaction.builder()
            .functionName("getBalance")
            .abiDefinitionList(abiDefinitionList2)
            .params(params)
            .build();
    String result = transaction.getEncodedFunction();
    Function function =
        new Function(
            "getBalance",
            Arrays.asList(new Address((String) params[0])),
            Arrays.asList(new TypeReference<Uint256>() {}));
    String expectedResult = FunctionEncoder.encode(function);
    Assertions.assertEquals(expectedResult, result);
  }
}
