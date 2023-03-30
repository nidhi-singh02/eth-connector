package hyperledger.besu.java.rest.client.utils;

import java.math.BigInteger;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.math.NumberUtils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.primitive.Double;
import org.web3j.abi.datatypes.primitive.Float;

/**
 * This utility class is used for converting inputParam in String datatype to datatype that can be
 * used for calling constructor of incoming clazz
 */
@UtilityClass
public class TypeConversionUtility {
  public static Object convert(Class clazz, String inputParam) {
    boolean isNumeric = NumberUtils.isCreatable(inputParam);
    if (isNumeric) {
      if (clazz.equals(Double.class)) {
        return java.lang.Double.parseDouble(inputParam);
      } else if (clazz.equals(Float.class)) {
        return java.lang.Float.parseFloat(inputParam);
      } else if (clazz.equals(Address.class)) {
        return inputParam;
      } else {
        return new BigInteger(inputParam);
      }
    } else {
      if (clazz.equals(Bool.class)) {
        return Boolean.valueOf(inputParam);
      }
    }
    return inputParam;
  }
}
