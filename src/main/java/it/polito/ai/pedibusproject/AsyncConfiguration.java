package it.polito.ai.pedibusproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
    @Bean("threadPoolTaskExecutor")
    public TaskExecutor getAsyncExecutor(@Value("${spring.async.core-pool-size}")int corePoolSize,
                                         @Value("${spring.async.max-pool-size}")int maxPoolSize,
                                         @Value("${spring.async.queue-capacity}")int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("Async-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }
}
