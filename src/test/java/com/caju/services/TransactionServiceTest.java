package com.caju.services;

import com.caju.entities.Account;
import com.caju.exceptions.AccountNotFoundException;
import com.caju.exceptions.UpdateAccountException;
import com.caju.dto.TransactionDTO;
import com.caju.entities.Transaction;
import com.caju.enums.MCCEnum;
import com.caju.repositories.TransactionRepository;
import com.caju.exceptions.InsufficientFundsTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        account = new Account();
        account.setId(1L);
        account.setName("Test Account");
        account.setCashBalance(BigDecimal.valueOf(100.0));
        account.setFoodBalance(BigDecimal.valueOf(100.0));
        account.setMealBalance(BigDecimal.valueOf(100.0));
    }

    @Test
    public void testTransactionAuthSimple_Success() throws AccountNotFoundException, UpdateAccountException, InsufficientFundsTransactionException {
        TransactionDTO transactionDto = new TransactionDTO(account.getId().toString(), BigDecimal.valueOf(100.0), "1234", "Test Merchant");

        when(accountService.getAccountById(account.getId().toString())).thenReturn(account);
        when(accountService.updateAccountBalance(account, BigDecimal.valueOf(100.0), MCCEnum.FOOD)).thenReturn(account);

        transactionService.authTransaction(transactionDto, true);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        // Assert that the transaction is created successfully
//        assertEquals(account.getId(), );
        // Assert other transaction fields
    }
}
