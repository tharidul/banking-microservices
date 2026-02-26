package com.bank.notificationservice.kafka.consumer;

import com.bank.notificationservice.kafka.event.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {

    @KafkaListener(
            topics = "transaction-events",
            groupId = "notification-group"
    )
    public void handleTransactionEvent(TransactionEvent event) {
        log.info("📩 Notification received for transaction: {}",
                event.getTransactionReference());

        switch (event.getTransactionType()) {
            case "DEPOSIT" -> log.info(
                    "✅ NOTIFICATION: Dear customer, your account {} " +
                            "has been credited with {}. Reference: {}",
                    event.getToAccountNumber(),
                    event.getAmount(),
                    event.getTransactionReference()
            );
            case "WITHDRAWAL" -> log.info(
                    "💸 NOTIFICATION: Dear customer, your account {} " +
                            "has been debited with {}. Reference: {}",
                    event.getFromAccountNumber(),
                    event.getAmount(),
                    event.getTransactionReference()
            );
            case "TRANSFER" -> log.info(
                    "🔄 NOTIFICATION: Dear customer, {} has been transferred " +
                            "from account {} to account {}. Reference: {}",
                    event.getAmount(),
                    event.getFromAccountNumber(),
                    event.getToAccountNumber(),
                    event.getTransactionReference()
            );
            default -> log.warn("⚠️ Unknown transaction type: {}",
                    event.getTransactionType());
        }
    }
}