package com.thecodealchemist.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thecodealchemist.main.entity.Transfer;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByAccountId(Long accountId); // Retrieve transfers for a specific user
}

