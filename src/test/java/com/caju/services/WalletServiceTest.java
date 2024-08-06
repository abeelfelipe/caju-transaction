package com.caju.services;

import com.caju.entities.Account;
import com.caju.entities.Wallet;
import com.caju.entities.WalletKey;
import com.caju.enums.CategoryWallet;
import com.caju.exceptions.IncorrectUpdateWallet;
import com.caju.exceptions.InsufficientFundsTransactionException;
import com.caju.exceptions.UpdateAccountException;
import com.caju.exceptions.WalletNotFoundException;
import com.caju.repositories.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository repository;

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
@DisplayName("Should get all wallets successfully")
void shouldGetAllWallets_Success() {
    List<Wallet> wallets = List.of(
            new Wallet(),
            new Wallet(),
            new Wallet()
    );
    when(repository.findAll()).thenReturn(wallets);

    List<Wallet> result = walletService.getAllWallets();

    assertThat(result).hasSize(3);
    verify(repository, times(1)).findAll();
}


@Test
@DisplayName("Should get all wallets by account successfully")
void shouldGetAllWalletsByAccount_Success() throws WalletNotFoundException {
    Long accountId = 123L;
    List<Wallet> wallets = List.of(
            new Wallet(),
            new Wallet(),
            new Wallet()
    );
    when(repository.findByAccountId(accountId)).thenReturn(Optional.of(wallets));

    List<Wallet> result = walletService.getAllWalletsByAccount(accountId);

    assertThat(result).hasSize(3);
    verify(repository, times(1)).findByAccountId(accountId);
}


@Test
@DisplayName("Should throw WalletNotFoundException when no wallets found")
void shouldThrowWalletNotFoundException_WhenNoWalletsFound() {
    Long accountId = 123L;
    when(repository.findByAccountId(accountId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> walletService.getAllWalletsByAccount(accountId))
            .isInstanceOf(WalletNotFoundException.class)
            .hasMessage("No wallets found for account id " + accountId);
    verify(repository, times(1)).findByAccountId(accountId);
}


@Test
@DisplayName("Should get wallet by ID successfully")
void shouldGetWalletById_Success() throws WalletNotFoundException {
    Account account = createAccountTest();
    WalletKey walletKey = new WalletKey(account, CategoryWallet.CASH);
    Wallet wallet = new Wallet();
    when(repository.findById(walletKey)).thenReturn(Optional.of(wallet));

    Wallet result = walletService.getWalletById(walletKey);

    assertThat(result).isSameAs(wallet);
    verify(repository, times(1)).findById(walletKey);
}


@Test
@DisplayName("Should throw WalletNotFoundException when wallet is not found")
void shouldThrowWalletNotFoundException_WhenWalletNotFound() {
    Account account = createAccountTest();
    WalletKey walletKey = new WalletKey(account, CategoryWallet.CASH);
    when(repository.findById(walletKey)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> walletService.getWalletById(walletKey))
            .isInstanceOf(WalletNotFoundException.class)
            .hasMessage("Wallets not found for key " + walletKey);
    verify(repository, times(1)).findById(walletKey);
}


    @Test
    @DisplayName("Update wallet food balance sucessful")
    void shouldUpdateAccountFoodBalance_Success() throws InsufficientFundsTransactionException, UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet {
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(50);
        Wallet walletFood = createWalletTest(account, CategoryWallet.FOOD, BigDecimal.valueOf(50));
        when(repository.findById(new WalletKey(account, CategoryWallet.FOOD))).thenReturn(Optional.of(walletFood));

        walletService.updateAccountBalanceWallet(account, totalAmount, CategoryWallet.FOOD);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(repository, times(1)).save(walletCaptor.capture());
        Wallet updatedWallet = walletCaptor.getValue();
        assertThat(updatedWallet.getBalance()).usingComparator(BigDecimal::compareTo).isZero();
        assertThat(updatedWallet.getId().getCategory()).isEqualTo(CategoryWallet.FOOD);
        assertThat(updatedWallet.getId().getAccount()).isSameAs(account);
    }

    @Test
    @DisplayName("Update wallet meal balance sucessful")
    void shouldUpdateAccountMealBalance_Success() throws InsufficientFundsTransactionException, UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet {
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(50);
        Wallet walletMeal = createWalletTest(account, CategoryWallet.MEAL, BigDecimal.valueOf(50));
        when(repository.findById(new WalletKey(account, CategoryWallet.MEAL))).thenReturn(Optional.of(walletMeal));

        walletService.updateAccountBalanceWallet(account, totalAmount, CategoryWallet.MEAL);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(repository, times(1)).save(walletCaptor.capture());
        Wallet updatedWallet = walletCaptor.getValue();
        assertThat(updatedWallet.getBalance()).usingComparator(BigDecimal::compareTo).isZero();
        assertThat(updatedWallet.getId().getCategory()).isEqualTo(CategoryWallet.MEAL);
        assertThat(updatedWallet.getId().getAccount()).isSameAs(account);
    }

    @Test
    @DisplayName("Update wallet cash balance sucessful")
    void shouldUpdateAccountCashBalance_Success() throws InsufficientFundsTransactionException, UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet {
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(50);
        Wallet walletCash = createWalletTest(account, CategoryWallet.CASH, BigDecimal.valueOf(50));
        when(repository.findById(new WalletKey(account, CategoryWallet.CASH))).thenReturn(Optional.of(walletCash));

        walletService.updateAccountBalanceWallet(account, totalAmount, CategoryWallet.CASH);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(repository, times(1)).save(walletCaptor.capture());
        Wallet updatedWallet = walletCaptor.getValue();
        assertThat(updatedWallet.getBalance()).usingComparator(BigDecimal::compareTo).isZero();
        assertThat(updatedWallet.getId().getCategory()).isEqualTo(CategoryWallet.CASH);
        assertThat(updatedWallet.getId().getAccount()).isSameAs(account);
    }

    @Test
    @DisplayName("Update with fallback should update cash balance successfully when food balance is insufficient")
    void shouldUpdateWalletCashWhenFoodBalanceIsInsufficient() throws InsufficientFundsTransactionException, UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet {
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(100);
        Wallet walletFood = createWalletTest(account, CategoryWallet.FOOD, BigDecimal.valueOf(50));
        Wallet walletCash = createWalletTest(account, CategoryWallet.CASH, BigDecimal.valueOf(100));
        when(repository.findById(new WalletKey(account, CategoryWallet.FOOD))).thenReturn(Optional.of(walletFood));
        when(repository.findById(new WalletKey(account, CategoryWallet.CASH))).thenReturn(Optional.of(walletCash));

        walletService.updateAccountBalanceWalletWithFallback(account, totalAmount, CategoryWallet.FOOD);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(repository, times(1)).save(walletCaptor.capture());
        Wallet updatedWallet = walletCaptor.getValue();
        assertThat(updatedWallet.getBalance()).usingComparator(BigDecimal::compareTo).isZero();
        assertThat(updatedWallet.getId().getCategory()).isEqualTo(CategoryWallet.CASH);
        assertThat(updatedWallet.getId().getAccount()).isSameAs(account);
    }

    @Test
    @DisplayName("Update with fallback should update cash balance successfully when meal balance is insufficient")
    void shouldUpdateWalletCashWhenMealBalanceIsInsufficient() throws InsufficientFundsTransactionException, UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet {
        Account account = createAccountTest();
        BigDecimal totalAmount = BigDecimal.valueOf(100);
        Wallet walletFood = createWalletTest(account, CategoryWallet.MEAL, BigDecimal.valueOf(50));
        Wallet walletCash = createWalletTest(account, CategoryWallet.CASH, BigDecimal.valueOf(100));
        when(repository.findById(new WalletKey(account, CategoryWallet.MEAL))).thenReturn(Optional.of(walletFood));
        when(repository.findById(new WalletKey(account, CategoryWallet.CASH))).thenReturn(Optional.of(walletCash));

        walletService.updateAccountBalanceWalletWithFallback(account, totalAmount, CategoryWallet.MEAL);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(repository, times(1)).save(walletCaptor.capture());
        Wallet updatedWallet = walletCaptor.getValue();
        assertThat(updatedWallet.getBalance()).usingComparator(BigDecimal::compareTo).isZero();
        assertThat(updatedWallet.getId().getCategory()).isEqualTo(CategoryWallet.CASH);
        assertThat(updatedWallet.getId().getAccount()).isSameAs(account);
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
