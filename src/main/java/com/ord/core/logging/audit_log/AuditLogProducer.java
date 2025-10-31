package com.ord.core.logging.audit_log;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogProducer  {

    private final KafkaTemplate<String, AuditLogDto> kafkaTemplate;
    private static final String TOPIC = "audit-logs";

    public void send(AuditLogDto dto) {
        kafkaTemplate.send(TOPIC, dto);
    }
}
