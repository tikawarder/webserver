package databaseserver.services.kafka;

import databaseserver.repository.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxMessageStatusService {

    private final OutboxMessageRepository outboxMessageRepository;

    // REQUIRES_NEW: each message commits independently, so one message's
    // failure can never roll back another's already-committed processed=true.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markProcessed(Long messageId) {
        outboxMessageRepository.findById(messageId)
                .ifPresent(message -> message.setProcessed(true));
    }
}
