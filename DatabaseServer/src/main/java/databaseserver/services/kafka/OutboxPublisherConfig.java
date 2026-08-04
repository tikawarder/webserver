package databaseserver.services.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class OutboxPublisherConfig {

    // Named threads make the pool's concurrency visible in logs/thread dumps.
    @Bean(destroyMethod = "shutdown")
    public ExecutorService outboxPublisherExecutor(
            @Value("${outbox.publisher.thread-pool-size:5}") int poolSize) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "outbox-publisher-" + counter.getAndIncrement());
            }
        };
        return Executors.newFixedThreadPool(poolSize, threadFactory);
    }
}
