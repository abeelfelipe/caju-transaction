package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.AccountNotFoundException;
import com.caju.repositories.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
