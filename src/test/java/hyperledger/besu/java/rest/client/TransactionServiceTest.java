package hyperledger.besu.java.rest.client;

import static hyperledger.besu.java.rest.client.exception.ErrorCode.HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR;

import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import hyperledger.besu.java.rest.client.exception.ErrorConstants;
import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import hyperledger.besu.java.rest.client.model.TransactionResponseModel;
import hyperledger.besu.java.rest.client.service.TransactionService;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.web3j.EVMTest;
import org.web3j.NodeType;

@EVMTest(type = NodeType.BESU)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionServiceTest {

  private static final String getputContractByteCode = "src/test/resources/get-put-state-bytecode";
  private static final String NFTByteCode = "src/test/resources/NFT-bytecode";

  @Autowired private TransactionService transactionService;
  private MockMultipartFile mockMultipartFile;
  private String functionName;
  private String contractAddress;
  private String contractAddressGetPut;
  private String contractAddressNFT;

  @BeforeAll
  void init() throws Exception {
    String compiledCode =
        FileUtils.readFileToString(new File(getputContractByteCode), StandardCharsets.UTF_8);
    ClientResponseModel response = transactionService.deploy(compiledCode);
    contractAddressGetPut = (String) response.getMessage();
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);

    String NFTCode = FileUtils.readFileToString(new File(NFTByteCode), StandardCharsets.UTF_8);
    response = transactionService.deploy(NFTCode);
    contractAddressNFT = (String) response.getMessage();
  }

  @Test
  void testDeployment() throws IOException {
    String compiledCode =
        FileUtils.readFileToString(new File(getputContractByteCode), StandardCharsets.UTF_8);
    ClientResponseModel response = transactionService.deploy(compiledCode);
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);
    Assertions.assertNotNull(response.getMessage());
  }

  @Test
  void testExecutionBurn() throws Exception {
    mockMultipartFile =
        new MockMultipartFile(
            "NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));
    functionName = "burn";
    String[] params = {String.valueOf(BigInteger.valueOf(1))};
    ClientResponseModel response =
        transactionService.execute(mockMultipartFile, contractAddress, functionName, params);
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);
  }

  @Test
  void testExecutionMint() throws Exception {

    mockMultipartFile =
        new MockMultipartFile(
            "NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));
    functionName = "mint";
    String[] params = {"0x1110000", String.valueOf(BigInteger.valueOf(1))};
    ClientResponseModel response =
        transactionService.execute(mockMultipartFile, contractAddress, functionName, params);
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);
  }

  @Test
  void testExecutionMintWithIncorrectInputSize() throws BesuTransactionException, IOException {

    mockMultipartFile =
        new MockMultipartFile(
            "NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));
    functionName = "mint";
    String[] params = {"0x1110000"};
    try {
      ClientResponseModel response =
          transactionService.execute(mockMultipartFile, contractAddress, functionName, params);
    } catch (BesuTransactionException e) {
      Assertions.assertEquals(e.getCode(), HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR);
      Assertions.assertEquals(
          e.getMessage(), "No of input params doesn't match number of inputs for the function");
    }
  }

  @Test
  void testExecutionMintWithIncorrectInputDataType() throws BesuTransactionException, IOException {
    mockMultipartFile =
        new MockMultipartFile(
            "NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));
    functionName = "mint";
    // Passing address in incorrect format
    // First parameter is of type address
    String[] params = {"abcID", String.valueOf(BigInteger.valueOf(2))};
    try {
      ClientResponseModel response =
          transactionService.execute(mockMultipartFile, contractAddress, functionName, params);
    } catch (BesuTransactionException e) {
      Assertions.assertEquals(e.getCode(), HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR);
      Assertions.assertEquals(
          e.getMessage(), "Unable to construct input type for function from given parameter input");
    }
  }

  @Test
  void testExecutionSet() throws Exception {
    mockMultipartFile =
        new MockMultipartFile(
            "get-put-state.abi",
            Files.newInputStream(new File("src/test/resources/get-put-state.abi").toPath()));
    functionName = "set";
    contractAddress = contractAddressGetPut;
    String[] param = {String.valueOf(BigInteger.valueOf(2))};
    ClientResponseModel response =
        transactionService.execute(mockMultipartFile, contractAddress, functionName, param);
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);
    TransactionResponseModel transactionResponseModel =
        (TransactionResponseModel) response.getMessage();
  }

  @Test
  void testReadWithoutParams() throws Exception {
    mockMultipartFile =
        new MockMultipartFile(
            "get-put-state.abi",
            Files.newInputStream(new File("src/test/resources/get-put-state.abi").toPath()));
    functionName = "get";
    contractAddress = contractAddressGetPut;
    ClientResponseModel response =
        transactionService.read(mockMultipartFile, contractAddress, functionName);
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);
    TransactionResponseModel transactionResponseModel =
        (TransactionResponseModel) response.getMessage();
  }

  @Test
  void testReadWithParams() throws Exception {
    mockMultipartFile =
        new MockMultipartFile(
            "NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));
    functionName = "balanceOf";
    String[] params = {"0x1110000"};
    ClientResponseModel response =
        transactionService.read(mockMultipartFile, contractAddressNFT, functionName, params);
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);
    TransactionResponseModel transactionResponseModel =
        (TransactionResponseModel) response.getMessage();
  }
}
