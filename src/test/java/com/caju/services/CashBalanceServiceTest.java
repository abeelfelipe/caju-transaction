package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.UpdateAccountException;
import com.caju.exceptions.InsufficientFundsTransactionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CashBalanceServiceTest {

    @InjectMocks
    private CashBalanceService cashBalanceService;

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
    @DisplayName("Test debit from cash account")
    public void testDebitFromCashAccount() {
        try {
            //given
            Account account = createAccountTest();
            //when
            Account accountUpdated = cashBalanceService.updateBalance(account, BigDecimal.valueOf(100L));
            //then
            assertThat(accountUpdated.getCashBalance()).isZero();
            assertThat(accountUpdated.getFoodBalance()).isEqualTo(account.getFoodBalance());
            assertThat(accountUpdated.getMealBalance()).isEqualTo(account.getMealBalance());
        } catch (InsufficientFundsTransactionException | UpdateAccountException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @DisplayName("Should update cash balance with fallback")
    public void shouldUpdateCashBalanceWithFallback() {
        try {

            // Arrange
            Account account = createAccountTest();
            account.setCashBalance(BigDecimal.valueOf(150L));
            // Act
            Account updatedAccount = cashBalanceService.updateBalanceWithFallback(account, BigDecimal.valueOf(120L));

            // Assert
            assertThat(updatedAccount.getFoodBalance()).isEqualTo(account.getFoodBalance());
            assertThat(updatedAccount.getMealBalance()).isEqualTo(account.getMealBalance());
            assertThat(updatedAccount.getCashBalance().toString()).isEqualTo("30.00");
        } catch (InsufficientFundsTransactionException | UpdateAccountException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @DisplayName("Test debit from cash account if balance is enough")
    public void debitFromCashAccountIfBalanceIsEnough() {
        try {

            Account account = createAccountTest();
            Account accountUpdated = cashBalanceService.updateBalanceWithFallback(account, BigDecimal.valueOf(90L));

            assertThat(accountUpdated.getCashBalance().toString()).isEqualTo("10.00");
            assertThat(accountUpdated.getFoodBalance()).isEqualTo(account.getFoodBalance());
            assertThat(accountUpdated.getMealBalance()).isEqualTo(account.getMealBalance());
        } catch (InsufficientFundsTransactionException | UpdateAccountException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @DisplayName("Test for insufficient funds exception when balance is not enough")
    public void shouldThrowInsufficientFundsExceptionWhenBalanceIsNotEnough() {
        // given
        Account account = createAccountTest();
        // when, then
        assertThrows(InsufficientFundsTransactionException.class,
                () -> cashBalanceService.updateBalance(account, BigDecimal.valueOf(101L)));
    }


    @Test
    @DisplayName("Test for NullPointerException when updating account balance")
    public void testNullPointerExceptionWhenUpdatingAccountBalance() throws InsufficientFundsTransactionException {
        //given
        Account account = createAccountTest();
        //then
        assertThrows(UpdateAccountException.class, () -> cashBalanceService.updateBalance(null, null));
        assertThrows(UpdateAccountException.class, () -> cashBalanceService.updateBalance(null, BigDecimal.valueOf(101L)));
        assertThrows(UpdateAccountException.class, () -> cashBalanceService.updateBalance(account, null));
    }


    @Test
    @DisplayName("Test for NullPointerException when updating account balance with fallback")
    public void testNullPointerExceptionWhenUpdatingAccountBalanceWithFallback() throws InsufficientFundsTransactionException {
        //given
        Account account = createAccountTest();
        //then
        assertThrows(UpdateAccountException.class, () -> cashBalanceService.updateBalanceWithFallback(null, null));
        assertThrows(UpdateAccountException.class, () -> cashBalanceService.updateBalanceWithFallback(null, BigDecimal.valueOf(101L)));
        assertThrows(UpdateAccountException.class, () -> cashBalanceService.updateBalanceWithFallback(account, null));
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
