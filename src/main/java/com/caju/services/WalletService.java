package com.caju.services;

import com.caju.dto.WalletDTO;
import com.caju.entities.Account;
import com.caju.entities.Wallet;
import com.caju.entities.WalletKey;
import com.caju.enums.CategoryWallet;
import com.caju.exceptions.AccountNotFoundException;
import com.caju.exceptions.IncorrectUpdateWallet;
import com.caju.exceptions.InsufficientFundsTransactionException;
import com.caju.exceptions.UpdateAccountException;
import com.caju.exceptions.WalletNotFoundException;
import com.caju.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.caju.utils.Utils.MENOR;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private AccountService accountService;

    /**
     * Retrieves a list of all wallets from the repository.
     *
     * @return  a list of Wallet objects representing all wallets in the repository
     */
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    /**
     * Retrieves a list of all wallets associated with a given account ID.
     *
     * @param  idAccount   the ID of the account to retrieve wallets for
     * @return             a list of Wallet objects associated with the account
     * @throws WalletNotFoundException   if no wallets are found for the account ID
     */
    public List<Wallet> getAllWalletsByAccount(Long idAccount) throws WalletNotFoundException {
        return walletRepository.findByAccountId(idAccount).orElseThrow(() -> new WalletNotFoundException(String.format("No wallets found for account id %s", idAccount)));
    }

    /**
     * Retrieves a Wallet object from the repository by its unique identifier.
     *
     * @param  walletKey   the unique identifier of the Wallet object
     * @return             the Wallet object with the specified identifier
     * @throws WalletNotFoundException   if no Wallet object is found with the specified identifier
     */
    public Wallet getWalletById(WalletKey walletKey) throws WalletNotFoundException {
        return walletRepository.findById(walletKey).orElseThrow(() -> new WalletNotFoundException(String.format("Wallets not found for key %s", walletKey)));
    }

    /**
     * Creates a new wallet based on the provided WalletDTO.
     *
     * @param  walletDto   the WalletDTO containing the account ID and category
     * @throws AccountNotFoundException     if the account is not found
     */
    public void createWallet(WalletDTO walletDto) throws AccountNotFoundException {
        Account account = accountService.getAccountById(walletDto.idAccount());
        Wallet wallet = Wallet.builder()
                .id(new WalletKey(account, walletDto.category()))
                .balance(walletDto.balance()).build();

        walletRepository.save(wallet);
    }

    /**
     * Updates the wallet if it exists in the repository.
     *
     * @param  wallet  the wallet to update
     * @throws WalletNotFoundException if the wallet is not found in the repository
     */
    private void updateWallet(Wallet wallet) throws WalletNotFoundException {
        if(Objects.nonNull(wallet) && Objects.nonNull(wallet.getId()) && Objects.nonNull(this.getWalletById(wallet.getId()))) {
            walletRepository.save(wallet);
        }
    }

    /**
     * Credits the specified amount to the wallet for the given account.
     *
     * @param  walletDto   the wallet DTO containing the account ID and category
     * @throws AccountNotFoundException     if the account is not found
     * @throws UpdateAccountException       if there is an error updating the account
     * @throws WalletNotFoundException      if the wallet is not found
     * @throws IncorrectUpdateWallet        if there is an error updating the wallet balance
     */
    public void creditInWallet(WalletDTO walletDto) throws AccountNotFoundException, UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet {
        Account account = accountService.getAccountById(walletDto.idAccount());
        Wallet wallet = walletRepository.findById(new WalletKey(account, walletDto.category())).orElseThrow();
        updateBalanceAddCredits(wallet, walletDto.balance());
    }

    /**
     * Updates the balance of a wallet for a given account.
     *
     * @param  account        the account for which the wallet balance is being updated
     * @param  totalAmount    the amount to debit from the wallet balance
     * @param  category       the category of the wallet to update
     * @throws UpdateAccountException     if the account, wallet, or amount to be updated is not identified
     * @throws WalletNotFoundException    if the wallet is not found
     * @throws IncorrectUpdateWallet      if there is an error updating the wallet
     * @throws InsufficientFundsTransactionException if the initial wallet does not have sufficient funds
     */
    public void updateAccountBalanceWallet(Account account, BigDecimal totalAmount, CategoryWallet category) throws UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet, InsufficientFundsTransactionException {
        Wallet wallet = getWalletById(new WalletKey(account, category));
        if(Objects.isNull(wallet) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account or amount to be updated.");
        BigDecimal currentBalance = wallet.getBalance();

        if(!isSufficientFunds(currentBalance, totalAmount)) {
            throw new InsufficientFundsTransactionException(String.format("Insufficient funds for transaction. Current balance for MEAL $%s - Transaction value $%s", currentBalance, totalAmount));
        }

        wallet.debit(totalAmount);
        updateWallet(wallet);
    }

    /**
     * Updates the balance of a wallet for a given account, falling back to a cash wallet if the initial wallet does not have sufficient funds.
     *
     * @param  account        the account for which the wallet balance is being updated
     * @param  totalAmount    the amount to debit from the wallet balance
     * @param  category       the category of the wallet to update
     * @throws UpdateAccountException     if the account, wallet, or amount to be updated is not identified
     * @throws WalletNotFoundException    if the wallet is not found
     * @throws IncorrectUpdateWallet      if there is an error updating the wallet
     * @throws InsufficientFundsTransactionException if the initial wallet does not have sufficient funds and the cash wallet does not have enough funds to cover the transaction
     */
    public void updateAccountBalanceWalletWithFallback(Account account, BigDecimal totalAmount, CategoryWallet category) throws UpdateAccountException, WalletNotFoundException, IncorrectUpdateWallet, InsufficientFundsTransactionException {
        Wallet wallet = getWalletById(new WalletKey(account, category));
        if(Objects.isNull(wallet) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account, wallet or amount to be updated.");
        BigDecimal currentBalance = wallet.getBalance();

        if(!isSufficientFunds(currentBalance, totalAmount)) {
            Wallet walletCash = getWalletById(new WalletKey(wallet.getId().getAccount(), CategoryWallet.CASH));
            BigDecimal currentBalanceCash = walletCash.getBalance();
            if(!isSufficientFunds(currentBalanceCash, totalAmount)) {
                throw new InsufficientFundsTransactionException(String.format("Insufficient funds for transaction. Current balance for FOOD $%s - Current balance for CASH $%s - Transaction value $%s", currentBalance, currentBalanceCash, totalAmount));
            }
            walletCash.debit(totalAmount);
            updateWallet(walletCash);
            return;
        }

        wallet.debit(totalAmount);
        updateWallet(wallet);
    }

    /**
     * Determines if the current balance is sufficient to cover the total amount.
     *
     * @param  currentBalance  the current balance of the account
     * @param  totalAmount     the total amount to be covered by the current balance
     * @return                 true if the current balance is sufficient, false otherwise
     */
    private boolean isSufficientFunds(BigDecimal currentBalance, BigDecimal totalAmount) {
        return currentBalance.subtract(totalAmount).compareTo(BigDecimal.ZERO) != MENOR;
    }

    /**
     * Updates the balance of a wallet by adding credits.
     *
     * @param  wallet        the wallet to update
     * @param  totalAmount   the amount of credits to add to the wallet balance
     * @throws WalletNotFoundException   if the wallet is not found
     * @throws UpdateAccountException    if the account, wallet, or amount to be updated is not identified
     * @throws IncorrectUpdateWallet     if there is an error updating the wallet
     */
    public void updateBalanceAddCredits(Wallet wallet, BigDecimal totalAmount) throws WalletNotFoundException, UpdateAccountException, IncorrectUpdateWallet {
        if(Objects.isNull(wallet) || Objects.isNull(totalAmount)) throw new UpdateAccountException("Unable to identify the account, wallet or amount to be updated.");
        wallet.credit(totalAmount);
        updateWallet(wallet);
    }

}
