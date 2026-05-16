package databaseserver.services.kafka;

import databaseserver.model.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private static final String TOPIC = "user-created";
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        log.info("Sending UserCreatedEvent to Kafka: {}", event);
        kafkaTemplate.send(TOPIC, String.valueOf(event.getId()), event);
    }
}
