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

public class MealBalanceServiceTest {
    @InjectMocks
    private MealBalanceService mealBalanceService;

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
@DisplayName("Debit correctly from Meal account")
public void debitMealAccount() {
    try {

        // Given
        Account account = createAccountTest();
        // When
        Account updatedAccount = mealBalanceService.updateBalance(account, BigDecimal.valueOf(100L));

        // Then
        assertThat(updatedAccount.getMealBalance()).isZero();
        assertThat(updatedAccount.getCashBalance()).isEqualTo(account.getCashBalance());
        assertThat(updatedAccount.getFoodBalance()).isEqualTo(account.getFoodBalance());
    } catch (InsufficientFundsTransactionException | UpdateAccountException e) {
        throw new RuntimeException(e);
    }
}


@Test
@DisplayName("Correctly debit from Cash account")
public void shouldDebitCorrectlyFromCashAccount() {
    try {

        // given
        Account account = createAccountTest();
        account.setCashBalance(BigDecimal.valueOf(150L));
        // when
        Account updatedAccount = mealBalanceService.updateBalanceWithFallback(account, BigDecimal.valueOf(120L));

        // then
        assertThat(updatedAccount.getFoodBalance()).isEqualTo(account.getFoodBalance());
        assertThat(updatedAccount.getMealBalance()).isEqualTo(account.getMealBalance());
        assertThat(updatedAccount.getCashBalance().toString()).isEqualTo("30.00");
    } catch (InsufficientFundsTransactionException | UpdateAccountException e) {
        throw new RuntimeException(e);
    }
}


@Test

@DisplayName("Debit correctly from Food account if there's enough balance")
public void debitFoodAccountIfSufficientBalance() {
    try {

        Account account = createAccountTest();
        account.setCashBalance(BigDecimal.valueOf(150L));
        Account updatedAccount = mealBalanceService.updateBalanceWithFallback(account, BigDecimal.valueOf(90L));

        assertThat(updatedAccount.getMealBalance().toString()).isEqualTo("10.00");
        assertThat(updatedAccount.getCashBalance()).isEqualTo(account.getCashBalance());
        assertThat(updatedAccount.getFoodBalance()).isEqualTo(account.getFoodBalance());
    } catch (Exception | InsufficientFundsTransactionException e) {
        throw new RuntimeException(e);
    }
}


@Test
@DisplayName("Throws InsufficientFundsTransactionException when balance is insufficient")
public void throwsExceptionWhenInsufficientBalance() throws InsufficientFundsTransactionException {
    // given
    Account account = createAccountTest();

    // then
    assertThrows(
        InsufficientFundsTransactionException.class,
        () -> mealBalanceService.updateBalance(account, BigDecimal.valueOf(101L))
    );
}

@Test
@DisplayName("Throws UpdateAccountException when null parameters are passed to updateBalance method")
public void testUpdateBalanceThrowsExceptionForNullParameters() throws InsufficientFundsTransactionException {
    //given
    Account account = createAccountTest();
    //then
    assertThrows(UpdateAccountException.class, () -> mealBalanceService.updateBalance(null, null));
    assertThrows(UpdateAccountException.class, () -> mealBalanceService.updateBalance(null, BigDecimal.valueOf(101L)));
    assertThrows(UpdateAccountException.class, () -> mealBalanceService.updateBalance(account, null));
}


@Test
@DisplayName("Test for NullPointerException when updating account balance with fallback")
public void testNullPointerExceptionWhenUpdatingAccountBalanceWithFallback() throws InsufficientFundsTransactionException {
    //given
    Account account = createAccountTest();
    //then
    assertThrows(UpdateAccountException.class, () -> mealBalanceService.updateBalanceWithFallback(null, null));
    assertThrows(UpdateAccountException.class, () -> mealBalanceService.updateBalanceWithFallback(null, BigDecimal.valueOf(101L)));
    assertThrows(UpdateAccountException.class, () -> mealBalanceService.updateBalanceWithFallback(account, null));
}


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
