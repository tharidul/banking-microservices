package com.bank.transactionservice.repository;

import com.bank.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(String transactionReference);
    List<Transaction> findByFromAccountNumberOrToAccountNumber(String fromAccount, String toAccount);
}
