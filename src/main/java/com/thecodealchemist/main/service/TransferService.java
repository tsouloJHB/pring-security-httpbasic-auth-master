package com.thecodealchemist.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thecodealchemist.main.entity.Transfer;
import com.thecodealchemist.main.repository.TransferRepository;

import java.util.List;

@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

    public Transfer saveTransfer(Transfer transfer) {
        return transferRepository.save(transfer);
    }
    

    public List<Transfer> getTransfersByAccountId(Long accountId) {
        return transferRepository.findByAccountId(accountId);
    }
}
