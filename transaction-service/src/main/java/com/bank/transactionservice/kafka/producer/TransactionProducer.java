package com.bank.transactionservice.kafka.producer;

import com.bank.transactionservice.kafka.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private static final String TOPIC = "transaction-events";

    public void publishTransactionEvent(TransactionEvent event) {
       log.info("Publishing transaction event: {}",event.getTransactionReference());
       kafkaTemplate.send(TOPIC, event.getTransactionReference(), event);
    }
}
