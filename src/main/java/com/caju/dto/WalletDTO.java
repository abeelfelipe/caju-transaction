package com.caju.dto;

import com.caju.enums.CategoryWallet;

import java.math.BigDecimal;

public record WalletDTO(String idAccount, BigDecimal balance, CategoryWallet category) {
}
