package com.bank.loanservice.repository;

import com.bank.loanservice.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByAccountNumber(String accountNumber);
}
