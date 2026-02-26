package com.bank.transactionservice.service;

import com.bank.transactionservice.client.AccountClient;
import com.bank.transactionservice.dto.request.TransactionRequest;
import com.bank.transactionservice.dto.response.TransactionResponse;
import com.bank.transactionservice.entity.Transaction;
import com.bank.transactionservice.entity.TransactionStatus;
import com.bank.transactionservice.entity.TransactionType;
import com.bank.transactionservice.kafka.event.TransactionEvent;
import com.bank.transactionservice.kafka.producer.TransactionProducer;
import com.bank.transactionservice.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;
    private final AccountClient accountClient;

    @CircuitBreaker(name = "account-service", fallbackMethod = "depositFallback")
    public TransactionResponse deposit(TransactionRequest request){

        accountClient.deposit(request.getToAccountNumber(), request.getAmount());

        Transaction transaction = Transaction.builder()
                .transactionReference(generateTransactionReference())
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .toAccountNumber(request.getToAccountNumber())
                .description(request.getDescription())
                .status(TransactionStatus.COMPLETED)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        transactionProducer.publishTransactionEvent(mapToEvent(savedTransaction));

        log.info("Transaction saved successfully: {}", savedTransaction.getTransactionReference());
        return mapToResponse(savedTransaction);

    }

    @CircuitBreaker(name = "account-service", fallbackMethod = "withdrawFallback")
    public TransactionResponse withdraw(TransactionRequest request){

        accountClient.withdraw(request.getFromAccountNumber(), request.getAmount());

        Transaction transaction = Transaction.builder()
                .transactionReference(generateTransactionReference())
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .fromAccountNumber(request.getFromAccountNumber())
                .description(request.getDescription())
                .status(TransactionStatus.COMPLETED)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);
        transactionProducer.publishTransactionEvent(mapToEvent(savedTransaction));

        log.info("Transaction saved successfully: {}", savedTransaction.getTransactionReference());
        return mapToResponse(savedTransaction);
    }

    @CircuitBreaker(name = "account-service", fallbackMethod = "transferFallback")
    public TransactionResponse transfer(TransactionRequest request){

        accountClient.withdraw(request.getFromAccountNumber(), request.getAmount());
        accountClient.deposit(request.getToAccountNumber(), request.getAmount());

        Transaction transaction = Transaction.builder()
                .transactionReference(generateTransactionReference())
                .transactionType(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .fromAccountNumber(request.getFromAccountNumber())
                .toAccountNumber(request.getToAccountNumber())
                .description(request.getDescription())
                .status(TransactionStatus.COMPLETED)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        transactionProducer.publishTransactionEvent(mapToEvent(savedTransaction));

        log.info("Transfer completed: {}" , savedTransaction.getTransactionReference());

        return mapToResponse(savedTransaction);

    }

    public List<TransactionResponse> getTransactionsByAccount(String accountNumber){
        return transactionRepository.findByFromAccountNumberOrToAccountNumber(accountNumber, accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionByReference(String reference){
        Transaction transaction = transactionRepository.findByTransactionReference(reference).orElseThrow(
                () -> new RuntimeException("Transaction not found for reference: " + reference)
        );
        return mapToResponse(transaction);
    }

    private String generateTransactionReference(){
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    private TransactionEvent mapToEvent(Transaction transaction){
        return TransactionEvent.builder()
                .transactionReference(transaction.getTransactionReference())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .description(transaction.getDescription())
                .build();
    }

    private TransactionResponse mapToResponse(Transaction transaction){

        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionReference(transaction.getTransactionReference())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public TransactionResponse depositFallback(TransactionRequest request, Exception e) {
        log.error("Circuit breaker triggered for deposit: {}", e.getMessage());
        throw new RuntimeException("Account service is currently unavailable. Please try again later.");
    }

    public TransactionResponse withdrawFallback(TransactionRequest request, Exception e) {
        log.error("Circuit breaker triggered for withdraw: {}", e.getMessage());
        throw new RuntimeException("Account service is currently unavailable. Please try again later.");
    }

    public TransactionResponse transferFallback(TransactionRequest request, Exception e) {
        log.error("Circuit breaker triggered for transfer: {}", e.getMessage());
        throw new RuntimeException("Account service is currently unavailable. Please try again later.");
    }

}
