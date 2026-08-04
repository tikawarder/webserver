package notificationservice.services.kafka;

import notificationservice.model.event.UserCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private Acknowledgment acknowledgment;

    private final KafkaConsumerService kafkaConsumerService = new KafkaConsumerService();

    @Test
    void listen_successfulProcessing_acknowledgesOffset() {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .id(1L)
                .name("Test Person")
                .city("Budapest")
                .build();
        ConsumerRecord<String, UserCreatedEvent> record = new ConsumerRecord<>("user-created", 0, 0L, "1", event);

        kafkaConsumerService.listen(record, acknowledgment);

        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void listen_processingThrows_neverAcknowledges() {
        // A null event value makes event.getName() throw a NullPointerException
        // inside the real listen() method, before it reaches acknowledge().
        ConsumerRecord<String, UserCreatedEvent> record = new ConsumerRecord<>("user-created", 0, 0L, "1", null);

        assertThrows(NullPointerException.class, () -> kafkaConsumerService.listen(record, acknowledgment));

        verify(acknowledgment, never()).acknowledge();
    }
}
