package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.InsufficientFundsTransactionException;
import com.caju.exceptions.UpdateAccountException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class FoodBalanceService implements IBalanceService {

    @Override
    public Account updateBalance(Account account, BigDecimal totalAmount) throws InsufficientFundsTransactionException, UpdateAccountException {
        if(Objects.isNull(account) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account or amount to be updated.");
        BigDecimal currentBalance = account.getFoodBalance();

        if(!isSufficientFunds(currentBalance, totalAmount)) {
            throw new InsufficientFundsTransactionException(String.format("Insufficient funds for transaction. Current balance for FOOD $%s - Transaction value $%s", currentBalance, totalAmount));
        }

        account.setFoodBalance(currentBalance.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP));
        return account;
    }

    @Override
    public Account updateBalanceWithFallback(Account account, BigDecimal totalAmount) throws InsufficientFundsTransactionException, UpdateAccountException {
        if(Objects.isNull(account) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account or amount to be updated.");
        BigDecimal currentBalanceFood = account.getFoodBalance();

        if(!isSufficientFunds(currentBalanceFood, totalAmount)) {
            BigDecimal currentBalanceCash = account.getCashBalance();
            if(!isSufficientFunds(currentBalanceCash, totalAmount)) {
                throw new InsufficientFundsTransactionException(String.format("Insufficient funds for transaction. Current balance for FOOD $%s - Current balance for CASH $%s - Transaction value $%s", currentBalanceFood, currentBalanceCash, totalAmount));
            }
            account.setCashBalance(currentBalanceCash.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP));
            return account;
        }

        account.setFoodBalance(currentBalanceFood.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP));
        return account;
    }
}
