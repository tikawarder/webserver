package databaseserver.services.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import databaseserver.model.entity.OutboxMessage;
import databaseserver.model.event.UserCreatedEvent;
import databaseserver.repository.OutboxMessageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxMessageRepository outboxMessageRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private OutboxMessageStatusService outboxMessageStatusService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ExecutorService executorService;

    private OutboxPublisher outboxPublisher;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(3);
        outboxPublisher = new OutboxPublisher(
                outboxMessageRepository, kafkaProducerService, outboxMessageStatusService, objectMapper, executorService);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    @DisplayName("Publishes multiple pending messages concurrently, on more than one thread")
    void publishPendingEvents_shouldUseMultipleThreads() throws Exception {
        int messageCount = 6;
        List<OutboxMessage> pending = buildPendingMessages(messageCount);
        when(outboxMessageRepository.findByProcessedFalseOrderByCreatedAtAsc()).thenReturn(pending);

        Set<String> threadNames = new CopyOnWriteArraySet<>();
        CountDownLatch latch = new CountDownLatch(messageCount);
        when(kafkaProducerService.sendUserCreatedEvent(any(UserCreatedEvent.class))).thenAnswer(invocation -> {
            threadNames.add(Thread.currentThread().getName());
            return CompletableFuture.completedFuture(null);
        });
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(outboxMessageStatusService).markProcessed(anyLong());

        outboxPublisher.publishPendingEvents();

        assertTrue(latch.await(5, TimeUnit.SECONDS), "all messages should have been processed");
        assertTrue(threadNames.size() > 1, "expected work spread across more than one thread, got: " + threadNames);
        verify(outboxMessageStatusService, times(messageCount)).markProcessed(anyLong());
    }

    @Test
    @DisplayName("One message's Kafka failure does not prevent the others from being marked processed")
    void publishPendingEvents_onePublishFailure_othersStillProcessed() {
        List<OutboxMessage> pending = buildPendingMessages(3);
        when(outboxMessageRepository.findByProcessedFalseOrderByCreatedAtAsc()).thenReturn(pending);

        AtomicInteger callCount = new AtomicInteger();
        when(kafkaProducerService.sendUserCreatedEvent(any(UserCreatedEvent.class))).thenAnswer(invocation -> {
            if (callCount.getAndIncrement() == 0) {
                return CompletableFuture.<SendResult<String, UserCreatedEvent>>failedFuture(new RuntimeException("broker unavailable"));
            }
            return CompletableFuture.completedFuture(null);
        });

        outboxPublisher.publishPendingEvents();

        // 3 messages in, 1 fails -> only 2 successful markProcessed calls
        verify(outboxMessageStatusService, times(2)).markProcessed(anyLong());
    }

    private List<OutboxMessage> buildPendingMessages(int count) {
        List<OutboxMessage> messages = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            messages.add(OutboxMessage.builder()
                    .id((long) i)
                    .eventType("USER_CREATED")
                    .payload("{\"id\":" + i + ",\"name\":\"Person" + i + "\"}")
                    .processed(false)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        return messages;
    }
}
