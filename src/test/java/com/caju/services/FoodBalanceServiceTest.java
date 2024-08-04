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

public class FoodBalanceServiceTest {

    @InjectMocks
    private FoodBalanceService foodBalanceService;

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
    @DisplayName("should correctly debit food balance from account")
    public void shouldCorrectlyDebitFoodBalanceFromAccount() {
        try {
            //given
            Account account = createAccountTest();
            //when
            Account accountUpdated = foodBalanceService.updateBalance(account, BigDecimal.valueOf(100L));
            //then
            assertThat(accountUpdated.getFoodBalance()).isZero();
            assertThat(accountUpdated.getCashBalance()).isEqualTo(account.getCashBalance());
            assertThat(accountUpdated.getMealBalance()).isEqualTo(account.getMealBalance());
        } catch (InsufficientFundsTransactionException | UpdateAccountException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should correctly debit cash balance from account")
    public void shouldCorrectlyDebitCashBalanceFromAccount() {
        try {

            // Arrange
            Account account = createAccountTest();
            account.setCashBalance(BigDecimal.valueOf(150L));

            // Act
            Account updatedAccount = foodBalanceService.updateBalanceWithFallback(account, BigDecimal.valueOf(120L));

            // Assert
            assertThat(updatedAccount.getFoodBalance()).isEqualTo(account.getFoodBalance());
            assertThat(updatedAccount.getMealBalance()).isEqualTo(account.getMealBalance());
            assertThat(updatedAccount.getCashBalance().toString()).isEqualTo("30.00");
        } catch (InsufficientFundsTransactionException | UpdateAccountException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should correctly debit food balance from account if there is enough balance")
    public void shouldCorrectlyDebitFoodBalanceFromAccountIfThereIsEnoughBalance() {
        try {

            // Arrange
            Account account = createAccountTest();
            account.setCashBalance(BigDecimal.valueOf(150L));

            // Act
            Account accountUpdated = foodBalanceService.updateBalanceWithFallback(account, BigDecimal.valueOf(90L));

            // Assert
            assertThat(accountUpdated.getFoodBalance().toString()).isEqualTo("10.00");
            assertThat(accountUpdated.getCashBalance()).isEqualTo(account.getCashBalance());
            assertThat(accountUpdated.getMealBalance()).isEqualTo(account.getMealBalance());


        } catch (InsufficientFundsTransactionException | UpdateAccountException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should throw InsufficientFundsTransactionException when balance is insufficient")
    public void shouldThrowInsufficientFundsExceptionWhenBalanceIsInsufficient() throws InsufficientFundsTransactionException {
        // Given
        Account account = createAccountTest();
        // When & Then
        assertThrows(InsufficientFundsTransactionException.class,
                () -> foodBalanceService.updateBalance(account, BigDecimal.valueOf(101L)));
    }

    @Test
    @DisplayName("Should throw UpdateAccountException when account is null or amount is null")
    public void shouldThrowUpdateAccountExceptionWhenAccountIsNullOrAmountIsNull() throws InsufficientFundsTransactionException {
        //given
        Account account = createAccountTest();

        //then
        assertThrows(UpdateAccountException.class, () -> foodBalanceService.updateBalance(null, null));
        assertThrows(UpdateAccountException.class, () -> foodBalanceService.updateBalance(null, BigDecimal.valueOf(101L)));
        assertThrows(UpdateAccountException.class, () -> foodBalanceService.updateBalance(account, null));
    }


    @Test
    @DisplayName("Should throw UpdateAccountException when updating account balance with fallback and null parameters")
    public void shouldThrowUpdateAccountExceptionWhenUpdatingWithNullParameters() throws InsufficientFundsTransactionException {
        //given
        Account account = createAccountTest();
        //then
        assertThrows(UpdateAccountException.class, () -> foodBalanceService.updateBalanceWithFallback(null, null));
        assertThrows(UpdateAccountException.class, () -> foodBalanceService.updateBalanceWithFallback(null, BigDecimal.valueOf(101L)));
        assertThrows(UpdateAccountException.class, () -> foodBalanceService.updateBalanceWithFallback(account, null));
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
