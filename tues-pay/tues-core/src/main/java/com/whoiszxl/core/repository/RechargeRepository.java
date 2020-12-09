package com.whoiszxl.core.repository;

import com.whoiszxl.core.entity.Recharge;

import java.math.BigDecimal;


public interface RechargeRepository extends BaseRepository<Recharge>{

    /**
     * 通过收款地址与货币名称与金额获取Recharge记录
     * @param toAddress 收款地址
     * @param currencyName 货币名称
     * @param amount 金额
     * @return 充值记录
     */
    Recharge getRechargeByToAddressAndCurrencyNameAndAmount(String toAddress, String currencyName, BigDecimal amount);

}
