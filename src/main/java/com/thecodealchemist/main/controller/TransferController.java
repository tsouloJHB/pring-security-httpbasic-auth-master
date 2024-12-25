package com.thecodealchemist.main.controller;

import jakarta.validation.Valid;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.thecodealchemist.main.entity.Deposit;
import com.thecodealchemist.main.entity.Transfer;
import com.thecodealchemist.main.model.AuthenticatedUser;
import com.thecodealchemist.main.repository.UserRepository;
import com.thecodealchemist.main.service.DepositService;
import com.thecodealchemist.main.service.TransferService;
import com.thecodealchemist.main.entity.User;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;
    @Autowired
    private DepositService depositService;

    @Autowired
    private UserRepository accountRepository;

   @PostMapping
public ResponseEntity<Map<String, Object>> createTransfer(@Valid @RequestBody Transfer transfer) {
    System.out.println("Account Name: ");
    System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    // Get the authenticated user's details
    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = user.getUsername();

    // Check if the user is authenticated
    if ("anonymousUser".equals(username)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "User is not authenticated."));
    }

    System.out.println("Authenticated username: " + username);

    // Find the user by email or cell number
    User account = accountRepository.findByEmail(username);

    if (account == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Account not found."));
    }

    // Validate sufficient balance
    if (account.getBalance() < transfer.getAmount()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Insufficient balance for the transfer, please deposit funds."));
    }

    // Deduct the transfer amount from the user's balance
    Long newBalance = account.getBalance() - transfer.getAmount().longValue();
    account.setBalance(newBalance);

    // Save the updated user balance
    accountRepository.save(account);

    // update the receiver's balance
    User receiver = accountRepository.findByAccountNumber(transfer.getAccountNumber());
    System.out.println("receiver: " + receiver);
    if(receiver != null){
        System.out.println("reviver received");
        receiver.setBalance(receiver.getBalance() + transfer.getAmount().longValue());
        accountRepository.save(receiver);
        //save as deposit
        Deposit deposit = new Deposit();
        deposit.setAmount(transfer.getAmount());
        deposit.setAccountId(receiver.getId());
        deposit.setMethod("Transfer");
        deposit.setReference(transfer.getMyReference());
        deposit.setDateCreated(transfer.getDateCreated());
        depositService.saveDeposit(deposit);
    }
   

    System.out.println("New balance: " + newBalance);

    // Set the accountId in the transfer
    transfer.setAccountId(account.getId());
    transfer.setAccountNumber(account.getAccountNumber());
    System.out.println("Account Name: " + transfer.getAccountName());

    // Save the transfer
    transferService.saveTransfer(transfer);

    // Prepare the response
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Transfer created successfully!");
    response.put("updatedBalance", newBalance);

    return ResponseEntity.ok(response);
    }

    


    @GetMapping
    public ResponseEntity<List<Transfer>> getTransfers() {
        // Get the authenticated user's details
        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        System.out.println(username);

        // Find the user by email or cell number
        User account = accountRepository.findByEmail(username);
        System.out.println(account);
        // Retrieve all transfers associated with this account
        List<Transfer> transfers = transferService.getTransfersByAccountId(account.getId());
        System.out.println(transfers);

        return ResponseEntity.ok(transfers);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> depositFunds(@Valid @RequestBody Deposit deposit) {
        // Get the authenticated user's details
        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
    
        // Check if the user is authenticated
        if ("anonymousUser".equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User is not authenticated."));
        }
    
        // Find the user account
        User account = accountRepository.findByEmail(username);
    
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Account not found."));
        }
    
        // Validate deposit amount
        if (deposit.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Deposit amount must be greater than 0."));
        }
    
        // Update the user's balance
        Long newBalance = account.getBalance() + deposit.getAmount().longValue();
        account.setBalance(newBalance);

      
    
        // Save the updated user balance
        accountRepository.save(account);
    
        // Optionally save the deposit details for tracking
        deposit.setAccountId(account.getId());
        depositService.saveDeposit(deposit);
    
        // Prepare the response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deposit successful!");
        response.put("updatedBalance", newBalance);
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deposits")
    public ResponseEntity<List<Deposit>> getDeposits() {
        // Get the authenticated user's details
        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();

        // Find the user by email
        User account = accountRepository.findByEmail(username);

        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        // Retrieve all deposits associated with this account
        List<Deposit> deposits = depositService.getDepositsByAccountId(account.getId());

        return ResponseEntity.ok(deposits);
    }
    
  
}
