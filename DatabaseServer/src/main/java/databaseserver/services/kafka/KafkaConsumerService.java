package databaseserver.services.kafka;

import databaseserver.model.event.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "user-created", groupId = "user-service-group")
    public void listen(UserCreatedEvent event) {
        log.info("Received event from Kafka! New user registered: {} from {}", 
            event.getName(), event.getCity());
        log.info("Full event details: {}", event);
    }
}
