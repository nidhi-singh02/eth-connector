package hyperledger.besu.java.rest.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import hyperledger.besu.java.rest.client.dto.CustomMultipartFile;
import hyperledger.besu.java.rest.client.exception.BesuTransactionException;
import hyperledger.besu.java.rest.client.exception.ErrorConstants;
import hyperledger.besu.java.rest.client.model.ClientResponseModel;
import hyperledger.besu.java.rest.client.model.TransactionResponseModel;
import hyperledger.besu.java.rest.client.service.TransactionService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.internal.matchers.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.web3j.EVMTest;
import org.web3j.NodeType;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.AbiDefinition;

import static hyperledger.besu.java.rest.client.exception.ErrorCode.HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR;

@EVMTest(type = NodeType.BESU)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionServiceTest {

  private static final String compiledContract = "src/test/resources/compiled-smart-contract";
  private static final String getputContractByteCode = "src/test/resources/get-put-state-bytecode";
  private static final String NFTByteCode = "src/test/resources/NFT-bytecode";


  @Autowired private TransactionService transactionService;
  private MockMultipartFile mockMultipartFile;
  private String functionName;
  private String contractAddress;
  private String contractAddressGetPut;
  private ObjectMapper objectMapper;
  private String contractAddressNFT;
  @BeforeAll

  void init() throws  Exception{
    String compiledCode =
            FileUtils.readFileToString(new File(getputContractByteCode), StandardCharsets.UTF_8);
    ClientResponseModel response = transactionService.deploy(compiledCode);
    contractAddressGetPut = (String) response.getMessage();
    Assertions.assertEquals(response.getCode(), null);
    System.out.println("message: " + response.getMessage());

    String NFTCode =
            FileUtils.readFileToString(new File(NFTByteCode), StandardCharsets.UTF_8);
     response = transactionService.deploy(NFTCode);
     contractAddressNFT = (String) response.getMessage();
    System.out.println("message: " + response.getMessage());


  }

  @Test
  void testDeployment() throws IOException {
    String compiledCode =
        FileUtils.readFileToString(new File(compiledContract), StandardCharsets.UTF_8);
    ClientResponseModel response = transactionService.deploy(compiledCode);
    Assertions.assertEquals(response.getCode(), null);
    Assertions.assertNotNull(response.getMessage());
    System.out.println("message: " + response.getMessage());

  }

  @Test
  void testExecutionBurn() throws Exception {

    mockMultipartFile = new MockMultipartFile("NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));

    functionName = "burn";
    Object[] params = {new Uint256(BigInteger.valueOf(1))};
    ClientResponseModel response =
            transactionService.execute( mockMultipartFile, contractAddress, functionName, params);
    Assertions.assertEquals(response.getCode(), null);
    System.out.println("message: " + response.getMessage());

  }

  @Test
  void testExecutionMint() throws Exception {

     mockMultipartFile = new MockMultipartFile("NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));

    functionName = "mint";
    Object[] params = { new Address("0x1110000"), new Uint256(BigInteger.valueOf(1))};
    ClientResponseModel response =
            transactionService.execute( mockMultipartFile, contractAddress, functionName, params);
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);
    System.out.println("message: " + response.getMessage());
  }

  @Test
  void testExecutionMintWithIncorrectInputSize() throws BesuTransactionException, IOException {

    mockMultipartFile = new MockMultipartFile("NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));
    functionName = "mint";
    Object[] params = {"0x1110000"};
    try {
      ClientResponseModel response =
              transactionService.execute(mockMultipartFile, contractAddress, functionName, params);
    }catch(BesuTransactionException e){
      Assertions.assertEquals(e.getCode(), HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR);
      Assertions.assertEquals(e.getMessage(), "No of input params doesn't match number of inputs for the function");
    }

  }

  @Test
  void testExecutionMintWithIncorrectInputDataType() throws BesuTransactionException, IOException {
    mockMultipartFile = new MockMultipartFile("NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));

    functionName = "mint";
    Object[] params = {0x1110000, new Uint256(BigInteger.valueOf(2))};
//    new Address("0x12345")
    try {
      ClientResponseModel response =
              transactionService.execute(mockMultipartFile, contractAddress, functionName, params);
    }catch(BesuTransactionException e){
      Assertions.assertEquals(e.getCode(), HYPERLEDGER_BESU_NO_ABI_DEFINITION_FOUND_ERROR);
      Assertions.assertEquals(e.getMessage(), "Input parameter class doesn't match class of corresponding input for the function");
    }

  }

  @Test
  void testExecutionSet() throws Exception {

    mockMultipartFile = new MockMultipartFile("get-put-state.abi", Files.newInputStream(new File("src/test/resources/get-put-state.abi").toPath()));
    functionName = "set";
    contractAddress = contractAddressGetPut;
    Object[] param = { new Uint256(BigInteger.valueOf(2))};
    System.out.println("contractAddress"+contractAddress);
    ClientResponseModel response =
            transactionService.execute(mockMultipartFile, contractAddress, functionName,param);
    Assertions.assertEquals(response.getCode(),ErrorConstants.NO_ERROR);
    System.out.println("message: " + response.getMessage());
  }
  @Test
  void testReadWithoutParams() throws Exception {

    mockMultipartFile = new MockMultipartFile("get-put-state.abi", Files.newInputStream(new File("src/test/resources/get-put-state.abi").toPath()));

    functionName = "get";
    contractAddress = contractAddressGetPut;
    System.out.println("contractAddress"+contractAddress);
    ClientResponseModel response =
            transactionService.read(mockMultipartFile, contractAddress, functionName);
    Assertions.assertEquals(response.getCode(),ErrorConstants.NO_ERROR);
    System.out.println("message: " + response.getMessage());
  }

  @Test
  void testReadWithParams() throws Exception {
    mockMultipartFile = new MockMultipartFile("NFT.abi", Files.newInputStream(new File("src/test/resources/NFT.abi").toPath()));
    functionName = "balanceOf";
    Object[] params = { new Address("0x1110000")};
    ClientResponseModel response =
            transactionService.read(mockMultipartFile, contractAddressNFT, functionName, params);
    Assertions.assertEquals(response.getCode(), ErrorConstants.NO_ERROR);

//    TransactionResponseModel transactionResponseModel = (TransactionResponseModel) response.getMessage();
//    Serializable message = response.getMessage();
//    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//    ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
//    oos.writeObject(message);
//    oos.close();

//    System.out.println("byteArrayOutputStream"+byteArrayOutputStream);
//    System.out.println("byteArrayOutputStream 2"+byteArrayOutputStream.toByteArray());
//
//    ByteArrayInputStream byteInputStream
//            = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//    ObjectInputStream objectInputStream
//            = new ObjectInputStream(byteInputStream);
//    TransactionResponseModel transactionResponseModel = (TransactionResponseModel) objectInputStream.readObject();
//    objectInputStream.close();


    // 2nd method using ObjectMapper
//    byte[] serializedData =  serialize(response.getMessage());
//    System.out.println("serializedData"+serializedData);
//    objectMapper = new ObjectMapper();
//    TransactionResponseModel model =  objectMapper.readValue(serializedData, TransactionResponseModel.class);
//    System.out.println("serializedData"+model);


    byte[] serializedData =  serialize(response.getMessage());
    System.out.println("serializedData"+serializedData);
//   TransactionResponseModel transactionResponseModel1 = (TransactionResponseModel) des(serializedData);
   TransactionResponseModel transactionResponseModel1 = convert(serializedData, TransactionResponseModel.class);
    System.out.println("transactionResponseModel1: " + transactionResponseModel1);
    }


  public static <T> T convert(byte[] serializedData,Class<T> clazz) throws IOException, ClassNotFoundException {
    ByteArrayInputStream byteIn = new ByteArrayInputStream(serializedData);
    ObjectInputStream in = new ObjectInputStream(byteIn);
    Object obj = in.readObject();
    in.close();
    byteIn.close();
    return clazz.cast(obj);
  }
  public static Object des(byte[] serializedData) throws IOException, ClassNotFoundException {
    ByteArrayInputStream byteIn = new ByteArrayInputStream(serializedData);
    ObjectInputStream in = new ObjectInputStream(byteIn);
    Object obj = in.readObject();
    in.close();
    byteIn.close();
    return obj;
  }

  private static byte[] serialize(Serializable obj) throws IOException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(byteOut);
    out.writeObject(obj);
    out.close();
    byteOut.close();
    return byteOut.toByteArray();
  }

  }

