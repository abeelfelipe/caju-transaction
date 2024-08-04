package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.AccountNotFoundException;
import com.caju.exceptions.UpdateAccountException;
import com.caju.repositories.AccountRepository;
import com.caju.enums.MCCEnum;
import com.caju.exceptions.InsufficientFundsTransactionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository repository;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @DisplayName("Get account by id")
    public void testGetAccountById_AccountFound() throws AccountNotFoundException, AccountNotFoundException {
        Optional<Account> account = Optional.of(createAccountTest());

        when(repository.findById(123L)).thenReturn(account);

        Account accountTest = accountService.getAccountById("123");

        assertThat(accountTest).isEqualTo(account.get());
    }


    @Test
    @DisplayName("Get not found account by id")
    public void testGetAccountById_AccountNotFound() throws AccountNotFoundException {
        Optional<Account> account = Optional.of(createAccountTest());

        when(repository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById("123"));
    }

    @Test
    @DisplayName("Update account food balance sucessful")
    void test_updateAccountFoodBalance_Success() throws InsufficientFundsTransactionException, UpdateAccountException {
        // Arrange
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(50);
        MCCEnum mcc = MCCEnum.FOOD;

        IBalanceService balanceService = Mockito.mock(FoodBalanceService.class);
        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act
        when(balanceService.updateBalance(account, totalAmount)).thenCallRealMethod();
        accountService.updateAccountBalance(account, totalAmount, mcc);

        // Assert
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(repository, times(1)).save(accountCaptor.capture());
        Account updatedAccount = accountCaptor.getValue();
        assertThat(updatedAccount.getFoodBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.valueOf(50L));
        assertThat(updatedAccount.getCashBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getCashBalance());
        assertThat(updatedAccount.getMealBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getMealBalance());
    }

    @Test
    @DisplayName("Update account cash balance sucessful")
    void test_updateAccountCashBalance_Success() throws InsufficientFundsTransactionException, UpdateAccountException {
        // Arrange
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(50);
        MCCEnum mcc = MCCEnum.CASH;

        IBalanceService balanceService = Mockito.mock(CashBalanceService.class);
        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act
        when(balanceService.updateBalance(account, totalAmount)).thenCallRealMethod();
        accountService.updateAccountBalance(account, totalAmount, mcc);

        // Assert
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(repository, times(1)).save(accountCaptor.capture());
        Account updatedAccount = accountCaptor.getValue();
        assertThat(updatedAccount.getFoodBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getFoodBalance());
        assertThat(updatedAccount.getCashBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.valueOf(50L));
        assertThat(updatedAccount.getMealBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getMealBalance());
    }

    @Test
    @DisplayName("Update account meal balance sucessful")
    void test_updateAccountMealBalance_Success() throws InsufficientFundsTransactionException, UpdateAccountException {
        // Arrange
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(50);
        MCCEnum mcc = MCCEnum.MEAL;

        IBalanceService balanceService = Mockito.mock(MealBalanceService.class);
        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act
        when(balanceService.updateBalance(account, totalAmount)).thenCallRealMethod();
        accountService.updateAccountBalance(account, totalAmount, mcc);

        // Assert
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(repository, times(1)).save(accountCaptor.capture());
        Account updatedAccount = accountCaptor.getValue();
        assertThat(updatedAccount.getFoodBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getFoodBalance());
        assertThat(updatedAccount.getCashBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getCashBalance());
        assertThat(updatedAccount.getMealBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.valueOf(50L));
    }


    @Test
    @DisplayName("Insufficient funds when update account balance")
    void test_updateAccountBalance_InsufficientFunds() throws UpdateAccountException, InsufficientFundsTransactionException {
        // Arrange
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(150);
        MCCEnum mcc = MCCEnum.FOOD;

        IBalanceService balanceService = mock(IBalanceService.class);
        when(balanceService.updateBalance(account, totalAmount)).thenThrow(new InsufficientFundsTransactionException("Insufficient funds"));
        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(InsufficientFundsTransactionException.class, () -> accountService.updateAccountBalance(account, totalAmount, mcc));
    }

    @Test
    @DisplayName("generic exception when Update account balance")
    void test_updateAccountBalance_UpdateAccountException() throws UpdateAccountException, InsufficientFundsTransactionException {

        // Arrange
        Account account = createAccountTest();
        when(repository.findById(anyLong())).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(UpdateAccountException.class, () -> accountService.updateAccountBalance(account, null, MCCEnum.FOOD));
    }

    @Test
    @DisplayName("Update account food balance with fallback sucessful")
    void test_updateAccountFoodBalanceWithFallback_Success() throws InsufficientFundsTransactionException, UpdateAccountException {
        // Arrange
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(50);
        MCCEnum mcc = MCCEnum.FOOD;

        IBalanceService balanceService = mock(FoodBalanceService.class);
        when(balanceService.updateBalanceWithFallback(account, totalAmount)).thenCallRealMethod();
        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act
        accountService.updateAccountBalanceWithFallback(account, totalAmount, mcc);

        // Assert
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(repository, times(1)).save(accountCaptor.capture());
        Account updatedAccount = accountCaptor.getValue();
        assertThat(updatedAccount.getFoodBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.valueOf(50L));
        assertThat(updatedAccount.getCashBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getCashBalance());
        assertThat(updatedAccount.getMealBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getMealBalance());
    }

    @Test
    @DisplayName("Not funds in food when update account balance with fallback, but funds in cash")
    void test_notFundsInFoodUpdateBalanceWithFallback_Success() throws InsufficientFundsTransactionException, UpdateAccountException {
        // Arrange
        Account account = createAccountTest();
        account.setCashBalance(BigDecimal.valueOf(150L));
        BigDecimal totalAmount = BigDecimal.valueOf(150);
        MCCEnum mcc = MCCEnum.FOOD;

        IBalanceService balanceService = mock(FoodBalanceService.class);
        when(balanceService.updateBalanceWithFallback(account, totalAmount)).thenCallRealMethod();
        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act
        accountService.updateAccountBalanceWithFallback(account, totalAmount, mcc);

        // Assert
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(repository, times(1)).save(accountCaptor.capture());
        Account updatedAccount = accountCaptor.getValue();
        assertThat(updatedAccount.getFoodBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getFoodBalance());
        assertThat(updatedAccount.getCashBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.ZERO);
        assertThat(updatedAccount.getMealBalance()).usingComparator(BigDecimal::compareTo).isEqualTo(account.getMealBalance());
    }


    @Test
    @DisplayName("Insufficient funds in both balances when update account balance with fallback")
    void test_updateAccountBalanceWithFallback_InsufficientFunds() throws UpdateAccountException, InsufficientFundsTransactionException {
        // Arrange
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(150);
        MCCEnum mcc = MCCEnum.FOOD;

        IBalanceService balanceService = mock(IBalanceService.class);
        when(balanceService.updateBalanceWithFallback(account, totalAmount)).thenThrow(new InsufficientFundsTransactionException("Insufficient funds"));

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(InsufficientFundsTransactionException.class, () -> accountService.updateAccountBalanceWithFallback(account, totalAmount, mcc));
    }

    /**
     * Creates a test account with the given ID, name, food balance, meal balance, and cash balance.
     *
     * @return the created test account
     */
    private Account createAccountTest() {
        Account account = new Account();
        account.setId(Long.valueOf("123"));
        account.setName("Cajuzinho");
        account.setFoodBalance(BigDecimal.valueOf(100L));
        account.setMealBalance(BigDecimal.valueOf(100L));
        account.setCashBalance(BigDecimal.valueOf(100L));

        return account;
    }
}
