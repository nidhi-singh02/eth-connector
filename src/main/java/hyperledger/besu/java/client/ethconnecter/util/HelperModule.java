package hyperledger.besu.java.client.ethconnecter.util;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

public class HelperModule {

  private static final String PRIVATE_KEY_STRING = "PRIVATE_KEY";
  private static final String PUBLIC_KEY_STRING = "PUBLIC_KEY";

  private static final BigInteger PRIVATE_KEY =
              Numeric.toBigInt(PRIVATE_KEY_STRING);
  private static final BigInteger PUBLIC_KEY =
              Numeric.toBigInt(PUBLIC_KEY_STRING);
  private static final ECKeyPair KEY_PAIR =
            new ECKeyPair(PRIVATE_KEY, PUBLIC_KEY);
  public static final Credentials CREDENTIALS = Credentials.create(KEY_PAIR);

  private static final int SLEEP_DURATION = 15000;
  private static final int ATTEMPTS = 30;
  private static final HttpService httpService = new HttpService("http://RPC_SERVER:8545/");
  public static Web3j web3j = Web3j.build(httpService);

    public static String getSolidityBinary(String binaryName) throws Exception {
    return load(binaryName + ".bin");
  }

  public static String load(String filePath) throws URISyntaxException, IOException {
    URL url = HelperModule.class.getResource(filePath);
    byte[] bytes = Files.readAllBytes(Paths.get(url.toURI()));
    return new String(bytes);
  }

  public static Function createContractFunction(String functionName) {
    /* TODO :  Make input and output parameters as generic to work with all function calls.
     *  Remove this function hardcoding and make it generic
    */
    if (functionName.equals("inc") || functionName.equals("dec")) {
      return new Function(
          functionName,
          Collections.emptyList(),
          Collections.emptyList()); // Collections.singletonList(new TypeReference<Uint>() {}));
    }
    return new Function(
        functionName,
        Collections.emptyList(),
        Collections.singletonList(
            new TypeReference<
                Uint>() {}));
    // Collections.singletonList(new Uint(BigInteger.valueOf(0)))
  }

  public static TransactionReceipt waitForTransactionReceipt(String transactionHash)
      throws Exception {

    Optional<TransactionReceipt> transactionReceiptOptional =
        getTransactionReceipt(transactionHash);

    if (!transactionReceiptOptional.isPresent()) {
      System.out.println("Transaction receipt not generated after " + ATTEMPTS + " attempts");
      throw new Exception();
    }

    return transactionReceiptOptional.get();
  }

  private static Optional<TransactionReceipt> getTransactionReceipt(String transactionHash)
      throws Exception {

    Optional<TransactionReceipt> receiptOptional = sendTransactionReceiptRequest(transactionHash);
    for (int i = 0; i < ATTEMPTS; i++) {
      if (!receiptOptional.isPresent()) {
        Thread.sleep(SLEEP_DURATION);
        receiptOptional = sendTransactionReceiptRequest(transactionHash);
        System.out.println("receiptOptional" + i + " " + receiptOptional);
      } else {
        break;
      }
    }

    return receiptOptional;
  }

  private static Optional<TransactionReceipt> sendTransactionReceiptRequest(String transactionHash)
      throws Exception {
    EthGetTransactionReceipt transactionReceipt =
        web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get();

    return transactionReceipt.getTransactionReceipt();
  }

  public static BigInteger getNonce(String address) {
    EthGetTransactionCount ethGetTransactionCount ;
    try {
      ethGetTransactionCount =
          web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
    return ethGetTransactionCount.getTransactionCount();
  }

  public static String callSmartContractFunction(Function function, String contractAddress)
      throws Exception {
    String encodedFunction = FunctionEncoder.encode(function);
    org.web3j.protocol.core.methods.response.EthCall response =
        web3j
            .ethCall(
                Transaction.createEthCallTransaction(
                    CREDENTIALS.getAddress(), contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
            .sendAsync()
            .get();
    System.out.println("response: " + response + " " + response.getRawResponse());
    System.out.println("error: " + response.getError());
    System.out.println("result: " + response.getResult());

    return response.getValue();
  }
}
