package notificationservice.services.kafka;

import notificationservice.model.event.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "user-created", groupId = "user-service-group")
    public void listen(ConsumerRecord<String, UserCreatedEvent> record) {
        // Extract traceId from Kafka message header
        Header traceHeader = record.headers().lastHeader("traceId");
        if (traceHeader != null) {
            String traceId = new String(traceHeader.value(), StandardCharsets.UTF_8);
            MDC.put("traceId", traceId); // propagates traceId into every subsequent log line
        }

        try {
            UserCreatedEvent event = record.value();
            log.info("Received event from Kafka! New user registered: {} from {}",
                    event.getName(), event.getCity());
            log.info("Full event details: {}", event);
        } finally {
            MDC.clear(); // always clean up to prevent traceId leaking into the next message on this thread
        }
    }
}
