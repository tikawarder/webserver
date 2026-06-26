package databaseserver.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import databaseserver.AbstractIntegrationTest;
import databaseserver.model.dto.PersonDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end test for the Transactional Outbox → Kafka pipeline.
 *
 * Flow:
 *   POST /api/persons
 *     → PersonService saves Person + OutboxMessage in one transaction
 *     → OutboxPublisher polls every 5s and sends to "user-created" topic
 *     → This test's KafkaConsumer receives the message
 *     → Awaitility waits up to 15s for the async event
 *
 * Why this matters (interview talking point):
 *   The Outbox Pattern guarantees at-least-once delivery even if the service crashes
 *   between the DB write and the Kafka send. The DB commit IS the event — Kafka
 *   is just the transport.
 */
class PersonKafkaIT extends AbstractIntegrationTest {

    private static final String TOPIC = "user-created";

    @Autowired
    private ObjectMapper objectMapper;

    private KafkaConsumer<String, String> consumer;
    private final List<ConsumerRecord<String, String>> received = new java.util.concurrent.CopyOnWriteArrayList<>();
    private volatile Thread pollerThread;

    @BeforeEach
    void setUpConsumer() {
        consumer = new KafkaConsumer<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-group",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        ));
        consumer.subscribe(List.of(TOPIC));
        pollerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                consumer.poll(Duration.ofMillis(500)).forEach(received::add);
            }
        });
        pollerThread.setDaemon(true);
        pollerThread.start();
    }

    @AfterEach
    void tearDownConsumer() throws InterruptedException {
        pollerThread.interrupt();
        pollerThread.join(2000);
        consumer.close();
        received.clear();
    }

    @Test
    @DisplayName("Creating a person triggers a UserCreatedEvent on the Kafka 'user-created' topic via Outbox pattern.")
    void createPerson_shouldPublishKafkaEvent() throws Exception {
        PersonDto newPerson = new PersonDto();
        newPerson.setName("Kafka Test Person");
        newPerson.setBirthDay(LocalDate.of(1995, 3, 15));
        newPerson.setCity("Budapest");

        mockMvc.perform(post("/api/persons")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isOk());

        // OutboxPublisher runs every 5s — wait up to 15s for the event
        await().atMost(15, SECONDS).untilAsserted(() -> {
            assertThat(received).isNotEmpty();
            String payload = received.get(0).value();
            assertThat(payload).contains("Kafka Test Person");
        });
    }
}
