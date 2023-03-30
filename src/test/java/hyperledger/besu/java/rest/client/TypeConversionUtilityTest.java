package hyperledger.besu.java.rest.client;

import hyperledger.besu.java.rest.client.utils.TypeConversionUtility;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.primitive.Double;
import org.web3j.abi.datatypes.primitive.Float;

@SpringBootTest
class TypeConversionUtilityTest {

  @Test
  void testBoolean() {
    String input = String.valueOf(true);
    Boolean expectedResult = true;
    Object result = TypeConversionUtility.convert(Bool.class, input);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testDouble() {
    String input = String.valueOf(1.1);
    double expectedResult = 1.1;
    Object result = TypeConversionUtility.convert(Double.class, input);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testFloat() {
    String input = String.valueOf(1.2f);
    float expectedResult = 1.2f;
    Object result = TypeConversionUtility.convert(Float.class, input);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testAddress() {
    String input = "0x12345";
    String expectedResult = "0x12345";
    Object result = TypeConversionUtility.convert(Address.class, input);
    Assertions.assertEquals(expectedResult, result);
  }

  @Test
  void testUint() {
    String input = String.valueOf(BigInteger.valueOf(1));
    BigInteger expectedResult = BigInteger.valueOf(1);
    Object result = TypeConversionUtility.convert(Uint256.class, input);
    Assertions.assertEquals(expectedResult, result);
  }
}
