package com.bank.accountservice.service;

import com.bank.accountservice.dto.request.CreateAccountRequest;
import com.bank.accountservice.dto.response.AccountResponse;
import com.bank.accountservice.entity.Account;
import com.bank.accountservice.entity.AccountStatus;
import com.bank.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    //Create Account
    public AccountResponse createAccount(CreateAccountRequest request){

        String accountNumber = generateUniqeAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .userId(request.getUserId())
                .balance(BigDecimal.ZERO)
                .build();

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);


    }

    //Get All accounts by user

    public List<AccountResponse> getAccountsByUserId(Long userId){
        return accountRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //Get Account By Account Number
    public AccountResponse getAccountByAccountNumber(String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new RuntimeException("Account Not Found"));
        return mapToResponse(account);
    }

    //Get Account by ID
    public AccountResponse getAccountById(Long id){
        Account account = accountRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Account Not Found"));
        return mapToResponse(account);
    }

    //Close Account
    public AccountResponse closeAccount(Long id){
        Account account = accountRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Account Not Found"));
        account.setStatus(AccountStatus.CLOSED);
        return mapToResponse(accountRepository.save(account));
    }

    public AccountResponse deposit(String accountNumber, BigDecimal amount){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new RuntimeException("Account Not Found"));
        account.setBalance(account.getBalance().add(amount));
        return mapToResponse(accountRepository.save(account));
    }

    public AccountResponse withdraw(String accountNumber, BigDecimal amount){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new RuntimeException("Account Not Found"));

        if(account.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Insufficient Balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return mapToResponse(accountRepository.save(account));
    }


    private String generateUniqeAccountNumber(){
        String accountNumber;

        do{
            accountNumber = String.valueOf(1000000000L + new Random().nextLong(9000000000L));
        }while(accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private AccountResponse mapToResponse(Account account){
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .balance(account.getBalance())
                .userId(account.getUserId())
                .creaetdAt(account.getCreaetdAt())
                .build();
    }
}
