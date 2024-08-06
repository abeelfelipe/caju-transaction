package com.caju.utils;

import com.caju.enums.CategoryWallet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final int MAIOR = 1;
    public static final int IGUAL = 0;
    public static final int MENOR = -1;

    private static final String[] MERCHANTS_FOR_FOOD = {"comida","food", "eat", "restaurante", "padaria"};
    private static final String[] MERCHANTS_FOR_MEAL = {"mercado", "quitanda", "emporio","meal", "market"};

    private static final Pattern FOOD_MATCHER = Pattern.compile(String.join("|", MERCHANTS_FOR_FOOD), Pattern.CASE_INSENSITIVE);
    private static final Pattern MEAL_MATCHER = Pattern.compile(String.join("|", MERCHANTS_FOR_MEAL), Pattern.CASE_INSENSITIVE);

    public static final String getMccByMerchant(String merchant) {
        Matcher foodMatcher = FOOD_MATCHER.matcher(merchant);
        if (foodMatcher.find()) {
            return CategoryWallet.FOOD.getCodes().get(0);
        }

        Matcher mealMatcher = MEAL_MATCHER.matcher(merchant);
        if (mealMatcher.find()) {
            return CategoryWallet.MEAL.getCodes().get(0);
        }

        return null;
    }
}
