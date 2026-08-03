package databaseserver.ai.observability;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_call_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private String model;

    @Column(name = "prompt_hash", nullable = false)
    private String promptHash;

    @Column(name = "input_tokens")
    private Integer inputTokens;

    @Column(name = "output_tokens")
    private Integer outputTokens;

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs;

    @Column(name = "cost_usd")
    private BigDecimal costUsd;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
