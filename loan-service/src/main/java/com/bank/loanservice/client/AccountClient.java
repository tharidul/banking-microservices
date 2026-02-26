package com.bank.loanservice.client;

import com.bank.loanservice.dto.response.AccountResponse;
import com.bank.loanservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", fallback = AccountClient.AccountClientFallback.class)
public interface AccountClient {

    @GetMapping("/api/v1/accounts/number/{accountNumber}")
    ApiResponse<AccountResponse> getAccount(@PathVariable("accountNumber") String accountNumber);

    @Component
    class AccountClientFallback implements AccountClient {
        @Override
        public ApiResponse<AccountResponse> getAccount(String accountNumber) {
            throw new RuntimeException("Account service is unavailable. Please try again later.");
        }
    }
}