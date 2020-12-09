package com.whoiszxl.core.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Recharge充值关联表
 */
@Data
@Entity
public class Recharge implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String orderId;

    private Long currencyId;

    private String currencyName;

    private String txHash;

    private BigDecimal amount;

    private String fromAddress;

    private String toAddress;

    private Date upchainAt;

    private Date upchainSuccessAt;

    private Integer upchainStatus;

    private Integer height;

    private Date createdAt;

    private Date updatedAt;


}
