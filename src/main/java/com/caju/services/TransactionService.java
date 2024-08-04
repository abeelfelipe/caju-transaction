package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.AccountNotFoundException;
import com.caju.dto.TransactionDTO;
import com.caju.entities.Transaction;
import com.caju.enums.MCCEnum;
import com.caju.enums.TransactionResponseEnum;
import com.caju.repositories.TransactionRepository;
import com.caju.exceptions.InsufficientFundsTransactionException;
import com.caju.dto.ResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountService accountService;

    /**
     * Retrieves all transactions from the repository.
     *
     * @return  A list of Transaction objects representing all transactions.
     */
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    /**
     * Authenticates a transaction by updating the account balance and saving the transaction details.
     *
     * @param transactionDTO the transaction details including the account ID, total amount, MCC, and merchant
     * @param isConsiderMerchantForMCC a flag indicating whether to consider the merchant for MCC category
     * @return a ResponseDTO object containing the transaction status code
     * @throws InsufficientFundsTransactionException if the account balance is insufficient for the transaction
     * @throws AccountNotFoundException if the account ID is not found
     */
    @Transactional
    public ResponseDTO authTransaction(TransactionDTO transactionDTO, boolean isConsiderMerchantForMCC) {
        try {
            Account account = accountService.getAccountById(transactionDTO.account());
            account = accountService.updateAccountBalance(account, transactionDTO.totalAmount(), identifierMCCCategory(transactionDTO.mcc(), transactionDTO.merchant(), isConsiderMerchantForMCC));

            Transaction transaction = Transaction.builder()
                    .account(account)
                    .totalAmount(transactionDTO.totalAmount())
                    .mcc(transactionDTO.mcc())
                    .merchant(transactionDTO.merchant())
                    .build();

            repository.save(transaction);

            return new ResponseDTO(TransactionResponseEnum.APPROVED.getCode(), "Transaction approved");
        } catch (InsufficientFundsTransactionException insufficientFundsTransactionException) {
            return new ResponseDTO(TransactionResponseEnum.INSUFFICIENT_FUNDS.getCode(), "Transaction rejected: %s".formatted(insufficientFundsTransactionException.getMessage()));
        } catch (Exception | AccountNotFoundException exception) {
            return new ResponseDTO(TransactionResponseEnum.ERROR.getCode(), "Transaction error: %s".formatted(exception.getMessage()));
        }
    }

    /**
     * Authenticates a transaction with fallback.
     *
     * @param  transactionDTO            the transaction data transfer object
     * @param  isConsiderMerchantForMCC  flag indicating whether to consider the merchant for MCC
     * @return                           the response data transfer object
     */
    @Transactional
    public ResponseDTO authTransactionWithFallback(TransactionDTO transactionDTO, boolean isConsiderMerchantForMCC) {
        try {
            Account account = accountService.getAccountById(transactionDTO.account());
            account = accountService.updateAccountBalanceWithFallback(account, transactionDTO.totalAmount(), identifierMCCCategory(transactionDTO.mcc(), transactionDTO.merchant(), isConsiderMerchantForMCC));

            Transaction transaction = Transaction.builder()
                    .account(account)
                    .totalAmount(transactionDTO.totalAmount())
                    .mcc(transactionDTO.mcc())
                    .merchant(transactionDTO.merchant())
                    .build();

            repository.save(transaction);

            return new ResponseDTO(TransactionResponseEnum.APPROVED.getCode(), "Transaction approved");
        } catch (InsufficientFundsTransactionException insufficientFundsTransactionException) {
            return new ResponseDTO(TransactionResponseEnum.INSUFFICIENT_FUNDS.getCode(), "Transaction rejected: %s".formatted(insufficientFundsTransactionException.getMessage()));
        } catch (Exception | AccountNotFoundException exception) {
            return new ResponseDTO(TransactionResponseEnum.ERROR.getCode(), "Transaction error: %s".formatted(exception.getMessage()));
        }
    }


    private static MCCEnum identifierMCCCategory(String mccTransaction, String merchantTransaction, boolean considerMerchant) {
        if (MCCEnum.FOOD.getCategoryCodes().contains(mccTransaction) || (considerMerchant &&
                MCCEnum.FOOD.getCompatibleMerchants().stream().anyMatch(compatibleMerchants -> merchantTransaction.toLowerCase().contains(compatibleMerchants.toLowerCase())))) {
            return MCCEnum.FOOD;
        }

        if (MCCEnum.MEAL.getCategoryCodes().contains(mccTransaction) || (considerMerchant &&
                MCCEnum.MEAL.getCompatibleMerchants().stream().anyMatch(compatibleMerchants -> merchantTransaction.toLowerCase().contains(compatibleMerchants.toLowerCase())))) {
            return MCCEnum.MEAL;
        }

        return MCCEnum.CASH;
    }

}
