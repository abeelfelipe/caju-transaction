package com.caju.utils;

import com.caju.services.CategoryWallet;

public class CategoryUtils {
    public static final CategoryWallet identifierCategoryByMcc(String mcc) {
        return switch (mcc) {
            case "5411","5412" -> CategoryWallet.FOOD;
            case "5811", "5812" -> CategoryWallet.MEAL;
            default -> CategoryWallet.CASH;
        };
    }

    public static final CategoryWallet identifierCategoryByMerchant(String merchant) {
        return switch (merchant) {
            case "comida","food", "eat" -> CategoryWallet.FOOD;
            case "restaurante", "mercado", "padaria", "meal" -> CategoryWallet.MEAL;
            default -> CategoryWallet.CASH;
        };
    }
}
