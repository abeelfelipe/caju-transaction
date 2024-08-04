package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.UpdateAccountException;
import com.caju.exceptions.InsufficientFundsTransactionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class MealBalanceService implements IBalanceService {

    @Override
    public Account updateBalance(Account account, BigDecimal totalAmount) throws InsufficientFundsTransactionException, UpdateAccountException {
        if(Objects.isNull(account) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account or amount to be updated.");
        BigDecimal currentBalance = account.getMealBalance();

        if(!isSufficientFunds(currentBalance, totalAmount)) {
            throw new InsufficientFundsTransactionException(String.format("Insufficient funds for transaction. Current balance for MEAL $%s - Transaction value $%s", currentBalance, totalAmount));
        }

        account.setMealBalance(currentBalance.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP));
        return account;
    }

    @Override
    public Account updateBalanceWithFallback(Account account, BigDecimal totalAmount) throws InsufficientFundsTransactionException, UpdateAccountException {
        if(Objects.isNull(account) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account or amount to be updated.");
        BigDecimal currentBalanceMeal = account.getFoodBalance();

        if(!isSufficientFunds(currentBalanceMeal, totalAmount)) {
            BigDecimal currentBalanceCash = account.getCashBalance();
            if(!isSufficientFunds(currentBalanceCash, totalAmount)) {
                throw new InsufficientFundsTransactionException(String.format("Insufficient funds for transaction. Current balance for MEAL $%s - Current balance for CASH $%s - Transaction value $%s", currentBalanceMeal, currentBalanceCash, totalAmount));
            }
            account.setCashBalance(currentBalanceCash.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP));
            return account;
        }

        account.setMealBalance(currentBalanceMeal.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP));
        return account;
    }

}
