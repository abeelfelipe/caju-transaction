package com.caju.entities;

import com.caju.enums.CategoryWallet;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class WalletKey implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_account", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CategoryWallet category;
}
