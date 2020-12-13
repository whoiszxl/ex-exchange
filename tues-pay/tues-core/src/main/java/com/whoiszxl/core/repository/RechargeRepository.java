package com.whoiszxl.core.repository;

import com.whoiszxl.core.entity.Recharge;

import java.math.BigDecimal;
import java.util.List;


public interface RechargeRepository extends BaseRepository<Recharge>{

    /**
     * 通过收款地址与货币名称与金额获取Recharge记录
     * @param toAddress 收款地址
     * @param currencyName 货币名称
     * @param amount 金额
     * @return 充值记录
     */
    Recharge getRechargeByToAddressAndCurrencyNameAndAmount(String toAddress, String currencyName, BigDecimal amount);


    /**
     * 通过订单ID和货币名称获取Recharge记录
     * @param orderId 订单ID
     * @param currencyName 货币名称
     * @return
     */
    Recharge getRechargeByOrderIdAndCurrencyName(String orderId, String currencyName);

    /**
     * 通过货币名称和上链状态获取充值单列表
     * @param currencyName 货币名称
     * @param upchainStatus 上链状态
     * @return
     */
    List<Recharge> findRechargesByCurrencyNameAndUpchainStatus(String currencyName, Integer upchainStatus);

}
