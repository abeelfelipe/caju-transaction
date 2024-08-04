package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.InsufficientFundsTransactionException;
import com.caju.exceptions.UpdateAccountException;

import java.math.BigDecimal;

import static com.caju.utils.Utils.MENOR;

public interface IBalanceService {
    /**
     * Updates the balance of an account.
     *
     * @param  account     the account to update the balance
     * @param  totalAmount the total amount to update the balance
     * @return             the updated account with the balance updated
     * @throws InsufficientFundsTransactionException if the account does not have enough funds to complete the transaction
     * @throws UpdateAccountException                 if there is an error updating the account
     */
    Account updateBalance(Account account, BigDecimal totalAmount) throws InsufficientFundsTransactionException, UpdateAccountException;
    /**
     * Updates the balance of an account with a fallback mechanism.
     *
     * @param  account     the account to update the balance
     * @param  totalAmount the total amount to update the balance
     * @return             the updated account with the balance updated
     * @throws InsufficientFundsTransactionException if the account does not have enough funds to complete the transaction
     * @throws UpdateAccountException                 if there is an error updating the account
     */
    Account updateBalanceWithFallback(Account account, BigDecimal totalAmount) throws InsufficientFundsTransactionException, UpdateAccountException;
        /**
         * Determines if the current balance is sufficient to cover the total amount.
         *
         * @param  currentBalance  the current balance of the account
         * @param  totalAmount     the total amount to be covered by the current balance
         * @return                 true if the current balance is sufficient, false otherwise
         */
    default boolean isSufficientFunds(BigDecimal currentBalance, BigDecimal totalAmount) {
        return currentBalance.subtract(totalAmount).compareTo(BigDecimal.ZERO) != MENOR;
    }
}
