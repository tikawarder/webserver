package databaseserver.ai.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RagIngestionConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService ragIngestionExecutor(
            @Value("${rag.ingestion.thread-pool-size:4}") int poolSize) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "rag-ingestion-" + counter.getAndIncrement());
            }
        };
        return Executors.newFixedThreadPool(poolSize, threadFactory);
    }
}
