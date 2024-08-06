package com.caju.controllers;

import com.caju.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService service;

    @Operation(summary = "Get all accounts")
    @GetMapping("all")
    public ResponseEntity getAllAccounts() {
        return ResponseEntity.ok(service.getAllAccounts());
    }
}
