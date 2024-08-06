package com.caju.services;

import com.caju.exceptions.AccountNotFoundException;
import com.caju.entities.Account;
import com.caju.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    /**
     * Retrieves an Account object from the repository based on the provided id.
     *
     * @param  id   the identifier of the account to retrieve
     * @return      the Account object with the given id
     * @throws AccountNotFoundException if no account is found with the given id
     */
    public Account getAccountById(String id) throws AccountNotFoundException {
        return repository.findById(Long.valueOf(id)).orElseThrow(() -> new AccountNotFoundException(String.format("No account found for id %s", id)));
    }
}
