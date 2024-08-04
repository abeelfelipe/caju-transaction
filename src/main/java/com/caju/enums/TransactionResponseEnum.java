package com.caju.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionResponseEnum {
    APPROVED("00"),
    ERROR("07"),
    INSUFFICIENT_FUNDS("51");

    String code;
}
