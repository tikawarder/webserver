package databaseserver.services.kafka;

import databaseserver.model.event.UserCreatedEvent;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private static final String TOPIC = "user-created";
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private final Tracer tracer; // Micrometer Tracer – injected by Spring

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        // Build a ProducerRecord so we can attach headers
        ProducerRecord<String, UserCreatedEvent> record =
                new ProducerRecord<>(TOPIC, String.valueOf(event.getId()), event);

        // If there is an active trace (there will be when triggered by an HTTP request), propagate it
        var currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            record.headers().add("traceId", traceId.getBytes(StandardCharsets.UTF_8));
            log.info("Sending UserCreatedEvent to Kafka with traceId: {}", traceId);
        } else {
            log.info("Sending UserCreatedEvent to Kafka (no active trace)");
        }

        kafkaTemplate.send(record);
    }
}
