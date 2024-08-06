package com.caju.controllers;

import com.caju.dto.TransactionDTO;
import com.caju.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @Operation(summary = "Get all transactions")
    @GetMapping("transaction/all")
    public ResponseEntity getAllTransactions() {
        return ResponseEntity.ok(service.getAllTransactions());
    }

    @Operation(summary = "Authenticates a transaction with fallback")
    @PostMapping("transaction/with-fallback")
    public ResponseEntity authTransactionWithFallBack(@RequestBody TransactionDTO transaction) {
        return ResponseEntity.ok(service.createTransactionWithFallback(transaction, false));
    }

    @Operation(summary = "Authenticates a transaction")
    @PostMapping("transaction/")
    public ResponseEntity authTransaction(@RequestBody TransactionDTO transaction) {
        return ResponseEntity.ok(service.createTransaction(transaction, false));
    }

    @Operation(summary = "Authenticates a transaction with fallback consider merchant for MCC ")
    @PostMapping("/l2/transaction/with-fallback")
    public ResponseEntity authTransactionWithFallBackConsiderMerchantForMCC(@RequestBody TransactionDTO transaction) {
        return ResponseEntity.ok(service.createTransactionWithFallback(transaction, true));
    }

    @Operation(summary = "Authenticates a transaction consider merchant for MCC")
    @PostMapping("/l2/transaction/")
    public ResponseEntity authTransactionConsiderMerchantForMCC(@RequestBody TransactionDTO transaction) {
        return ResponseEntity.ok(service.createTransaction(transaction, true));
    }
}

