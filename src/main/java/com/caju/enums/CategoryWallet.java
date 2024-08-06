package com.caju.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ToString
@AllArgsConstructor
@Getter
public enum CategoryWallet {
    FOOD(Arrays.asList("5411","5412")),
    MEAL(Arrays.asList("5811", "5812")),
    CASH(Arrays.asList(""));

    List<String> codes;

    public static CategoryWallet getCategoryByMcc(String code) {
        return Arrays.stream(values())
                .filter(categoryWallet -> categoryWallet.codes.contains(code))
                .findFirst()
                .orElse(CASH);
    }

}
