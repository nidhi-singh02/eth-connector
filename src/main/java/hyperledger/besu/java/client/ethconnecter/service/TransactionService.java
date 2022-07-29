package hyperledger.besu.java.client.ethconnecter.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.web3j.abi.datatypes.Type;

public interface TransactionService {

  Map<String, String> decode(String transactionHex);

  Map<String, String> execute(
      BigInteger gasPrice, BigInteger gasLimit, String contractAddress, String functionName);

  List<Type> call(String contractAddress, String functionName);
}
