package com.caju.entities;

import com.caju.services.CategoryWallet;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class WalletKey implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_account", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

    private CategoryWallet category;

}
