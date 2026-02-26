package com.bank.auditservice.kafka.consumer;

import com.bank.auditservice.entity.AuditLog;
import com.bank.auditservice.kafka.event.TransactionEvent;
import com.bank.auditservice.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditLogRepository auditLogRepository;

    @KafkaListener(topics = "transaction-events", groupId = "audit-group")
    public void handleTransactionEvent(TransactionEvent event) {
        log.info("Audit received for transaction: {}", event.getTransactionReference());

        AuditLog auditLog = AuditLog.builder()
                .transactionReference(event.getTransactionReference())
                .transactionType(event.getTransactionType())
                .amount(event.getAmount())
                .fromAccountNumber(event.getFromAccountNumber())
                .toAccountNumber(event.getToAccountNumber())
                .description(event.getDescription())
                .transactionCreatedAt(event.getCreatedAt())
                .auditedAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.info("Audit log saved for transaction: {}", event.getTransactionReference());
    }
}
