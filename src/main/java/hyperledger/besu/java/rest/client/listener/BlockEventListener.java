package hyperledger.besu.java.rest.client.listener;

import hyperledger.besu.java.rest.client.config.EthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlockEventListener implements Runnable {

    @Autowired private EthConfig ethConfig;

    @Override
    public void run() {
        // TODO: Improve the logic to include all nodes from the network
        // get events with all transactions data
        ethConfig.getWeb3jList().get(0).blockFlowable(true)
                .subscribe(ethBlock -> {
                    // consume the block and send it
                    // TODO: send this in a Kafka message
                    log.info("Block: {}", ethBlock.getBlock());
                    log.info("Block Result: {}", ethBlock.getResult());
                });
    }
}
