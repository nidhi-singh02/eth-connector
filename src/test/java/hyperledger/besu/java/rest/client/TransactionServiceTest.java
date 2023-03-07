package hyperledger.besu.java.rest.client;

import hyperledger.besu.java.rest.client.service.TransactionService;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.web3j.EVMTest;
import org.web3j.NodeType;

@EVMTest(type = NodeType.BESU)
@SpringBootTest
public class TransactionServiceTest {

  private static final String compiledContract = "compiled-smart-contract";

  @Autowired private TransactionService transactionService;

  @Test
  void testDeploymet() throws IOException {
    String compiledCode =
        FileUtils.readFileToString(new File(compiledContract), StandardCharsets.UTF_8);
    transactionService.deploy(compiledCode);
  }
}
