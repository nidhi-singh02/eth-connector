package hyperledger.besu.java.rest.client;

import hyperledger.besu.java.rest.client.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.web3j.EVMTest;
import org.web3j.NodeType;
import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@EVMTest(type = NodeType.BESU)
@SpringBootTest
public class TransactionServiceTest {

    private static final String compiledContract = "compiled-smart-contract";

    @Autowired private TransactionService transactionService;

    @Test
    void testDeploymet(
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider
    ) throws IOException {
        String compiledCode = FileUtils.readFileToString(new File(compiledContract), StandardCharsets.UTF_8);
        transactionService.deploy(compiledCode);
    }
}
