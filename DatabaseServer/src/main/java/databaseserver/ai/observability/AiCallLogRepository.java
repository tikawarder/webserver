package databaseserver.ai.observability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiCallLogRepository extends JpaRepository<AiCallLog, Long> {
}
