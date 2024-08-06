package com.caju.controllers;

import com.caju.dto.WalletDTO;
import com.caju.entities.Wallet;
import com.caju.exceptions.AccountNotFoundException;
import com.caju.exceptions.IncorrectUpdateWallet;
import com.caju.exceptions.UpdateAccountException;
import com.caju.exceptions.WalletNotFoundException;
import com.caju.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService service;

    @Operation(summary = "Get all wallets")
    @GetMapping("all")
    public ResponseEntity getAllWallets() {
        return ResponseEntity.ok(service.getAllWallets());
    }

    @Operation(summary = "Create wallets by account id")
    @PostMapping("/create")
    public ResponseEntity createWallet(WalletDTO walletDto) throws AccountNotFoundException {
        service.createWallet(walletDto);
        return ResponseEntity.created(null).build();
    }

    @Operation(summary = "Add credit in wallet")
    @PostMapping("/credit")
    public ResponseEntity addCreditInWallet(WalletDTO walletDto) throws AccountNotFoundException, UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet {
        service.creditInWallet(walletDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Find wallets by account id")
    @GetMapping("/{idAccount}")
    public ResponseEntity findWalletsByAccount(@PathVariable Long idAccount) throws WalletNotFoundException {
        return ResponseEntity.ok(service.getAllWalletsByAccount(idAccount));
    }
}
