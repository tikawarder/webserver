package databaseserver.services.kafka;

import databaseserver.model.event.UserCreatedEvent;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private static final String TOPIC = "user-created";
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private final Tracer tracer;

    // Returns the send future so callers can wait for the broker ack instead of
    // assuming success once this method returns (KafkaTemplate.send is async).
    public CompletableFuture<SendResult<String, UserCreatedEvent>> sendUserCreatedEvent(UserCreatedEvent event) {
        ProducerRecord<String, UserCreatedEvent> record =
                new ProducerRecord<>(TOPIC, String.valueOf(event.getId()), event);

        var currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            record.headers().add("traceId", traceId.getBytes(StandardCharsets.UTF_8));
            log.info("Sending UserCreatedEvent to Kafka with traceId: {}", traceId);
        } else {
            log.info("Sending UserCreatedEvent to Kafka (no active trace)");
        }

        return kafkaTemplate.send(record);
    }
}
