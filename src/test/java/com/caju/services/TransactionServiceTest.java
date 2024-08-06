package com.caju.services;

import com.caju.dto.ResponseDTO;
import com.caju.entities.Account;
import com.caju.entities.Wallet;
import com.caju.entities.WalletKey;
import com.caju.enums.CategoryWallet;
import com.caju.enums.TransactionResponseEnum;
import com.caju.exceptions.AccountNotFoundException;
import com.caju.exceptions.IncorrectUpdateWallet;
import com.caju.exceptions.UpdateAccountException;
import com.caju.dto.TransactionDTO;
import com.caju.entities.Transaction;
import com.caju.exceptions.WalletNotFoundException;
import com.caju.repositories.TransactionRepository;
import com.caju.exceptions.InsufficientFundsTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {

    @Mock
    private AccountService accountService;
    @Mock
    private WalletService walletService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;
    private Wallet walletCash;
    private Wallet walletFood;
    private Wallet walletMeal;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        account = new Account();
        account.setId(1L);
        account.setName("Test Account");
    }

    @Test
    @DisplayName("Create transaction when account cash is found and transaction is successful")
    public void testCreateTransactionWhenAccountCashIsFoundAndTransactionIsSuccessful() throws WalletNotFoundException, AccountNotFoundException, UpdateAccountException, IncorrectUpdateWallet, InsufficientFundsTransactionException {
        Account account = createAccountTest();
        Wallet walletCash = createWalletTest(account, CategoryWallet.CASH, new BigDecimal("100"));
        TransactionDTO transactionDTO = new TransactionDTO(account.getId().toString(), BigDecimal.valueOf(100L), "5000", "PADARIA DO ZE               SAO PAULO BR");
        when(accountService.getAccountById(account.getId().toString())).thenReturn(account);
        when(walletService.getWalletById(walletCash.getId())).thenReturn(walletCash);

        ResponseDTO response = transactionService.createTransaction(transactionDTO, false);

        assertEquals(TransactionResponseEnum.APPROVED.getCode(), response.code());
        verify(walletService, times(1)).updateAccountBalanceWallet(account, BigDecimal.valueOf(100L), CategoryWallet.CASH);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    @Test
    @DisplayName("Create transaction when account meal is found and transaction is successful")
    public void testCreateTransactionWhenAccountMealIsFoundAndTransactionIsSuccessful() throws WalletNotFoundException, AccountNotFoundException, UpdateAccountException, IncorrectUpdateWallet, InsufficientFundsTransactionException {
        Account account = createAccountTest();
        Wallet walletCash = createWalletTest(account, CategoryWallet.MEAL, new BigDecimal("100"));
        TransactionDTO transactionDTO = new TransactionDTO(account.getId().toString(), BigDecimal.valueOf(100L), "5811", "PADARIA DO ZE               SAO PAULO BR");
        when(accountService.getAccountById(account.getId().toString())).thenReturn(account);
        when(walletService.getWalletById(walletCash.getId())).thenReturn(walletCash);

        ResponseDTO response = transactionService.createTransaction(transactionDTO, false);

        assertEquals(TransactionResponseEnum.APPROVED.getCode(), response.code());
        verify(walletService, times(1)).updateAccountBalanceWallet(account, BigDecimal.valueOf(100L), CategoryWallet.MEAL);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }


    @Test
    @DisplayName("Create transaction when account food is found and transaction is successful")
    public void testCreateTransactionWhenAccountFoodIsFoundAndTransactionIsSuccessful() throws WalletNotFoundException, AccountNotFoundException, UpdateAccountException, IncorrectUpdateWallet, InsufficientFundsTransactionException {
        Account account = createAccountTest();
        Wallet walletCash = createWalletTest(account, CategoryWallet.FOOD, new BigDecimal("100"));
        TransactionDTO transactionDTO = new TransactionDTO(account.getId().toString(), BigDecimal.valueOf(100L), "5411", "PADARIA DO ZE               SAO PAULO BR");
        when(accountService.getAccountById(account.getId().toString())).thenReturn(account);
        when(walletService.getWalletById(walletCash.getId())).thenReturn(walletCash);

        ResponseDTO response = transactionService.createTransaction(transactionDTO, false);

        assertEquals(TransactionResponseEnum.APPROVED.getCode(), response.code());
        verify(walletService, times(1)).updateAccountBalanceWallet(account, BigDecimal.valueOf(100L), CategoryWallet.FOOD);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    /**
     * Creates a test account with the given ID, name, food balance, meal balance, and cash balance.
     *
     * @return the created test account
     */
    private Account createAccountTest() {
        Account account = new Account();
        account.setId(Long.valueOf("123"));
        account.setName("User for test");

        return account;
    }

    /**
     * Creates a test Wallet object with the given Account, CategoryWallet, and balance.
     *
     * @param account    the Account object associated with the Wallet
     * @param categoryWallet the CategoryWallet object associated with the Wallet
     * @param balance    the balance of the Wallet
     * @return the created Wallet object
     */
    private Wallet createWalletTest(Account account, CategoryWallet categoryWallet, BigDecimal balance) {
        Wallet walletTest = new Wallet();
        walletTest.setId(new WalletKey(account, categoryWallet));
        walletTest.setBalance(balance);

        return walletTest;
    }
}
