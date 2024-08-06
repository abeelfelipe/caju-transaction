package com.caju.repositories;

import com.caju.entities.Wallet;
import com.caju.entities.WalletKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findById(WalletKey walletKey);
    Optional<List<Wallet>> findByAccountId(Long idAccount);
}
