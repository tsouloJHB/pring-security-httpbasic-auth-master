package com.thecodealchemist.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thecodealchemist.main.entity.Deposit;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    // Additional custom queries (if needed) can be added here
    List<Deposit> findByAccountId(Long accountId); 
}

