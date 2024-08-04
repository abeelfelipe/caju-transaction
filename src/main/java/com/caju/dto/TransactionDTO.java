package com.caju.dto;

import java.math.BigDecimal;

public record TransactionDTO(String account, BigDecimal totalAmount, String mcc, String merchant) {
}
