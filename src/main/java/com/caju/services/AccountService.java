package com.caju.services;

import com.caju.exceptions.AccountNotFoundException;
import com.caju.entities.Account;
import com.caju.exceptions.UpdateAccountException;
import com.caju.repositories.AccountRepository;
import com.caju.enums.MCCEnum;
import com.caju.exceptions.InsufficientFundsTransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    /**
     * Retrieves an Account object from the repository based on the provided id.
     *
     * @param  id   the identifier of the account to retrieve
     * @return      the Account object with the given id
     * @throws AccountNotFoundException if no account is found with the given id
     */
    public Account getAccountById(String id) throws AccountNotFoundException {
        return repository.findById(Long.valueOf(id)).orElseThrow(() -> new AccountNotFoundException(String.format("No account found for id %s", id)));
    }

    /**
     * Updates the balance of an account by calling the appropriate balance service based on the MCCEnum.
     *
     * @param  account     the account to update the balance
     * @param  totalAmount the total amount to update the balance
     * @param  mcc         the MCCEnum determining which category to use
     * @throws InsufficientFundsTransactionException if the account does not have enough funds to complete the transaction
     * @throws UpdateAccountException                 if there is an error updating the account
     */
    public Account updateAccountBalance(Account account, BigDecimal totalAmount, MCCEnum mcc) throws InsufficientFundsTransactionException, UpdateAccountException {
        IBalanceService balanceService = getInstanceUpdaterBalance(mcc);
//        return balanceService.updateBalance(account, totalAmount);
        return repository.save(balanceService.updateBalance(account, totalAmount));
    }

    /**
     * Updates the balance of an account with fallback.
     *
     * @param  account      the account to update the balance
     * @param  totalAmount  the total amount to update the balance
     * @param  mcc          the MCCEnum to determine which category to use for update
     * @throws InsufficientFundsTransactionException if the account does not have enough funds
     * @throws UpdateAccountException                 if there is an error updating the account balance
     */
    public Account updateAccountBalanceWithFallback(Account account, BigDecimal totalAmount, MCCEnum mcc) throws InsufficientFundsTransactionException, UpdateAccountException {
        IBalanceService balanceService = getInstanceUpdaterBalance(mcc);
//        return balanceService.updateBalanceWithFallback(account, totalAmount);
        return repository.save(balanceService.updateBalanceWithFallback(account, totalAmount));
    }

    /**
     * Returns an instance of the IBalanceService based on the given MCCEnum.
     *
     * @param  mcc  the MCCEnum to determine which balance service to return
     * @return      an instance of the IBalanceService corresponding to the given MCCEnum
     */
    public static IBalanceService getInstanceUpdaterBalance(MCCEnum mcc) {
        switch (mcc) {
            case FOOD -> {
                return new FoodBalanceService();
            }
            case MEAL -> {
                return new MealBalanceService();
            }
            default -> {
                return new CashBalanceService();
            }
        }
    }
}
