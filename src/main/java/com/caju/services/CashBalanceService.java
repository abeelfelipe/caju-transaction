package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.UpdateAccountException;
import com.caju.exceptions.InsufficientFundsTransactionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class CashBalanceService implements IBalanceService {

    @Override
    public Account updateBalance(Account account, BigDecimal totalAmount) throws InsufficientFundsTransactionException, UpdateAccountException {
        if(Objects.isNull(account) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account or amount to be updated.");
        BigDecimal currentBalance = account.getCashBalance();

        if(!isSufficientFunds(currentBalance, totalAmount)) {
            throw new InsufficientFundsTransactionException(String.format("Insufficient funds for transaction. Current balance for CASH $%s - Transaction value $%s", currentBalance, totalAmount));
        }

        account.setCashBalance(currentBalance.subtract(totalAmount).setScale(2, RoundingMode.HALF_UP));
        return account;
    }

    /**
     * Updates the balance of an account. This method does not have a fallback mechanism. If the account does not have enough funds, it will throw an exception.
     *
     * @param  account      the account to update the balance
     * @param  totalAmount  the total amount to update the balance
     * @throws InsufficientFundsTransactionException if the account does not have enough funds
     * @throws UpdateAccountException                 if there is an error updating the account balance
     * @deprecated This method is deprecated. Use {@link #updateBalance(Account, BigDecimal)} instead.
     */
    @Deprecated
    public Account updateBalanceWithFallback(Account account, BigDecimal totalAmount) throws InsufficientFundsTransactionException, UpdateAccountException {
        if(Objects.isNull(account) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account or amount to be updated.");
        return updateBalance(account, totalAmount);
    }

}
