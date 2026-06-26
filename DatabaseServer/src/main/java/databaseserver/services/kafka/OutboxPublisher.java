package databaseserver.services.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import databaseserver.model.entity.OutboxMessage;
import databaseserver.model.event.UserCreatedEvent;
import databaseserver.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxMessageRepository outboxMessageRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    @Transactional
    public void publishPendingEvents() {
        List<OutboxMessage> pendingMessages = outboxMessageRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingMessages.isEmpty()) {
            return;
        }

        log.info("Found {} pending outbox messages to publish to Kafka.", pendingMessages.size());

        for (OutboxMessage message : pendingMessages) {
            try {
                if ("USER_CREATED".equals(message.getEventType())) {
                    UserCreatedEvent event = objectMapper.readValue(message.getPayload(), UserCreatedEvent.class);
                    kafkaProducerService.sendUserCreatedEvent(event);
                }

                // Mark as processed
                message.setProcessed(true);
                outboxMessageRepository.save(message);
                log.info("Successfully published outbox message with ID: {}", message.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox message with ID: {}. Error: {}", message.getId(), e.getMessage());
            }
        }
    }
}
