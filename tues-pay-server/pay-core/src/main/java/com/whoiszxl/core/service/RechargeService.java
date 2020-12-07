package com.whoiszxl.core.service;

import org.springframework.stereotype.Service;

@Service
public class RechargeService {

    /**
     * 校验地址是否存在于充值表
     * @param currencyId 币种ID
     * @param address 地址
     * @return 是否存在
     */
    public boolean checkAddressExist(String currencyId, String address) {

        //TODO

        return true;
    }


}
