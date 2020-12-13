package com.whoiszxl.core.service;

import com.whoiszxl.core.entity.CurrencyAccount;
import com.whoiszxl.core.entity.Height;
import com.whoiszxl.core.entity.Recharge;
import com.whoiszxl.core.enums.UpchainStatusEnum;
import com.whoiszxl.core.repository.AccountRepository;
import com.whoiszxl.core.repository.HeightRepository;
import com.whoiszxl.core.repository.RechargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RechargeService {

    @Autowired
    private HeightRepository heightRepository;

    @Autowired
    private RechargeRepository rechargeRepository;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * 通过货币名称获取当前同步的区块高度
     * @param currencyName 货币名称
     * @return 区块高度
     */
    public Height getCurrentHeight(String currencyName) {
        return heightRepository.getHeightByCurrencyName(currencyName);
    }

    /**
     * 通过收款地址与货币名称与金额获取Recharge记录
     * @param toAddress 收款地址
     * @param currencyName 货币名称
     * @param amount 金额
     * @return 充值记录
     */
    public Recharge getRecharge(String toAddress, String currencyName, BigDecimal amount) {
        return rechargeRepository.getRechargeByToAddressAndCurrencyNameAndAmount(toAddress, currencyName, amount);
    }

    /**
     * 通过货币名称和订单ID获取充值单记录
     * @param currencyName 货币名称
     * @param orderId 订单ID
     * @return
     */
    public Recharge getRechargeByOrderId(String currencyName, String orderId) {
        return rechargeRepository.getRechargeByOrderIdAndCurrencyName(orderId, currencyName);
    }


    /**
     * 更新recharge记录
     * @param recharge 记录
     */
    public void updateRecharge(Recharge recharge) {
        rechargeRepository.save(recharge);
    }

    /**
     * 更新当前区块高度
     * @param height 区块高度记录
     */
    public void saveCurrentHeight(Height height) {
        heightRepository.save(height);
    }


    /**
     * 更新或者新增充值记录
     * @param recharge 充值记录
     */
    public void saveRecharge(Recharge recharge) {
        rechargeRepository.save(recharge);
    }

    /**
     * 通过货币名称获取所有待确认的充值单
     * @param currencyName 货币名称
     */
    public List<Recharge> getWaitConfirmRecharge(String currencyName) {
        return rechargeRepository.findRechargesByCurrencyNameAndUpchainStatus(currencyName, UpchainStatusEnum.WAITING_CONFIRM.getCode());
    }


    /**
     * 更新或新增账号记录
     * @param account 地址账号
     */
    public void saveAccount(CurrencyAccount account) {
        accountRepository.save(account);
    }
}
