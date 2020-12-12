package com.whoiszxl.core.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Currency加密货币实体
 */
@ToString
@Data
@Entity
@Table(name = "pay_currency")
public class Currency implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String currencyName;

    private String currencyLogo;

    private String currencyType;

    private String currencyContent;

    private Integer currencyDecimalsNum;

    private String currencyUrl;

    private String contractAbi;

    private String contractAddress;

    private String coldAddress;

    private BigDecimal coldThreshold;

    private BigDecimal feeWithdraw;

    private String walletKey;

    private Integer confirms;

    private Integer status;

    private Date createdAt;

    private Date updatedAt;

}
