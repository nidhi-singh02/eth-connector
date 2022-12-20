package hyperledger.besu.java.rest.client.util;

import io.reactivex.Flowable;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Uint;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

public class HelperModule {

  private static final String PRIVATE_KEY_STRING = "PRIVATE_KEY";
  private static final String PUBLIC_KEY_STRING = "PUBLIC_KEY";

  private static final BigInteger PRIVATE_KEY = Numeric.toBigInt(PRIVATE_KEY_STRING);
  private static final BigInteger PUBLIC_KEY = Numeric.toBigInt(PUBLIC_KEY_STRING);
  private static final ECKeyPair KEY_PAIR = new ECKeyPair(PRIVATE_KEY, PUBLIC_KEY);
  public static final Credentials CREDENTIALS = Credentials.create(KEY_PAIR);

  private static final int SLEEP_DURATION = 15000;
  private static final int ATTEMPTS = 30;
  private static final HttpService httpService = new HttpService("http://RPC_SERVER:8545/");
  public static Web3j web3j = Web3j.build(httpService);

  public static ScheduledExecutorService scheduledExecutorService;
  private static final long POLLING_INTERVAL = 1000;
  public static Web3jService web3jService;
  public static JsonRpc2_0Web3j web3jRx =
      new JsonRpc2_0Web3j(web3jService, POLLING_INTERVAL, scheduledExecutorService);

}
