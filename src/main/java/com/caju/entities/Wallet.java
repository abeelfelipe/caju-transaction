package com.caju.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "wallets")
@NamedQueries({
        @NamedQuery(name = "Wallet.findByAccountId", query = "select w from wallets w where w.id.account.id = :idAccount")
})
public class Wallet implements Serializable {

    @EmbeddedId
    private WalletKey id;

    @Column
    private BigDecimal balance;

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}

