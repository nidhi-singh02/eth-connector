package hyperledger.besu.java.rest.client.service;

import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.listener.BlockEventListener;
import hyperledger.besu.java.rest.client.listener.SmartContractEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncEventListener {

  @Autowired private ApplicationContext appContext;

  @Autowired private TaskExecutor taskExecutor;
  @Autowired private EthEventsProperties ethEventsProperties;

  @EventListener(ApplicationReadyEvent.class)
  public void smartContractEventListenerThread() {
    SmartContractEventListener smartContractEventListener =
        appContext.getBean(SmartContractEventListener.class);
    taskExecutor.execute(smartContractEventListener);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void blockEventListenerThread() {
    if (ethEventsProperties.isBlock()) {
      BlockEventListener blockEventListener = appContext.getBean(BlockEventListener.class);
      taskExecutor.execute(blockEventListener);
    } else {
      log.info("Block event listening is currently disabled");
    }
  }
}
