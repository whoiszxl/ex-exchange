package com.whoiszxl.core.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "pay_currency_account")
public class CurrencyAccount implements Serializable {

    @Id
    private Long currencyId;

    private String currencyName;

    private String keystoreName;

    private String mnemonic;

    private String address;

    private Date createdAt;

    private Date updatedAt;

}
