package hyperledger.besu.java.rest.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorConfig {

  @Bean
  public TaskExecutor threadPoolTaskExecutor() {

    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(7);
    threadPoolTaskExecutor.setMaxPoolSize(7);
    threadPoolTaskExecutor.initialize();

    return threadPoolTaskExecutor;
  }
}
