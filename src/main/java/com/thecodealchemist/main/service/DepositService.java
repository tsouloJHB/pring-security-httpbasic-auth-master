package com.thecodealchemist.main.service;

import java.util.List;
// import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thecodealchemist.main.entity.Deposit;
import com.thecodealchemist.main.repository.DepositRepository;
@Service
public class DepositService {   

    @Autowired
    private DepositRepository depositRepository;

    public Deposit saveDeposit(Deposit deposit) {
        // Save the deposit record to the database
        return depositRepository.save(deposit);
    }
    public List<Deposit> getDepositsByAccountId(Long accountId) {
        return depositRepository.findByAccountId(accountId);
    }

    // public List<Deposit> getDepositsByAccountId(Long accountId) {
    //     // Example of retrieving deposits for a specific account
    //     return depositRepository.findAll().stream()
    //             .filter(deposit -> deposit.getAccountId().equals(accountId))
    //             .collect(Collectors.toList());
    // }
}
