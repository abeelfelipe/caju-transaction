package com.caju.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public enum MCCEnum {
    FOOD(Arrays.asList("5411","5412"), Arrays.asList("food", "eat", "comida","restaurante")),
    MEAL(Arrays.asList("5811", "5812"), Arrays.asList( "mercado", "padaria", "meal")),
    CASH(Collections.emptyList(), Collections.emptyList());

    List<String> categoryCodes;
    List<String> compatibleMerchants;
}
