package hyperledger.besu.java.rest.client.service;

import hyperledger.besu.java.rest.client.config.EthEventsProperties;
import hyperledger.besu.java.rest.client.listener.BlockEventListener;
import hyperledger.besu.java.rest.client.listener.SmartContractEventListener;
import java.util.Objects;
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

  /** using the application context to map the async listeners. */
  @Autowired private ApplicationContext appContext;

  /** This is for concurrrent task. */
  @Autowired private TaskExecutor taskExecutor;

  /** using eth-connector events properties. */
  @Autowired(required = false)
  private EthEventsProperties ethEventsProps;

  /** creating concurrent task for smart-contract. */
  @EventListener(ApplicationReadyEvent.class)
  public void sCEventListenerThread() {
    try {
      if (Objects.nonNull(ethEventsProps.getEventNameByHash())) {
        SmartContractEventListener smartContractEventListener =
            appContext.getBean(SmartContractEventListener.class);
        taskExecutor.execute(smartContractEventListener);
        log.info("SmartContract event listening is currently enabled ");
      }
    } catch (Exception e) {
      log.warn("Error while SC event listening {}", e.getMessage());
    }
  }

  /** creating concurrent task for block-events. */
  @EventListener(ApplicationReadyEvent.class)
  public void blockEvtListenerThread() {
    try {
      if (Objects.nonNull(ethEventsProps) && ethEventsProps.isBlock()) {

        BlockEventListener bkLsr = appContext.getBean(BlockEventListener.class);
        taskExecutor.execute(bkLsr);
        log.info("Block event listening is currently enabled ");
      }
    } catch (Exception e) {
      log.warn("Error while Block event listening {}", e.getMessage());
    }
  }
}
