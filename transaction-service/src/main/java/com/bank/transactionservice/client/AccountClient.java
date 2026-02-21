package com.bank.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "account-service")
public interface AccountClient {

    @PutMapping("/api/v1/accounts/{accountNumber}/deposit")
    void deposit(@PathVariable("accountNumber") String accountNumber,@RequestParam("amount") BigDecimal amount);

    @PutMapping("/api/v1/accounts/{accountNumber}/withdraw")
    void withdraw(@PathVariable("accountNumber") String accountNumber,@RequestParam("amount") BigDecimal amount);
}
