package databaseserver.services.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import databaseserver.model.entity.OutboxMessage;
import databaseserver.model.event.UserCreatedEvent;
import databaseserver.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaProducerService kafkaProducerService;
    private final OutboxMessageStatusService outboxMessageStatusService;
    private final ObjectMapper objectMapper;
    private final ExecutorService outboxPublisherExecutor;

    // No @Transactional here: it would hold a DB connection open for the whole
    // async publish-and-wait below, for no benefit — each message commits on its
    // own in OutboxMessageStatusService.
    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxMessage> pendingMessages = outboxMessageRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.info("Found {} pending outbox messages to publish to Kafka.", pendingMessages.size());

        List<CompletableFuture<Void>> futures = pendingMessages.stream()
                .map(message -> CompletableFuture.runAsync(() -> publishSingleMessage(message), outboxPublisherExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void publishSingleMessage(OutboxMessage message) {
        try {
            if ("USER_CREATED".equals(message.getEventType())) {
                UserCreatedEvent event = objectMapper.readValue(message.getPayload(), UserCreatedEvent.class);
                // Blocks a pool worker thread (not the scheduler thread) until the broker acks.
                kafkaProducerService.sendUserCreatedEvent(event).join();
            }

            outboxMessageStatusService.markProcessed(message.getId());
            log.info("Successfully published outbox message with ID: {} on thread {}",
                    message.getId(), Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("Failed to publish outbox message with ID: {}. Error: {}", message.getId(), e.getMessage());
        }
    }
}
