package com.whoiszxl.core.service;

import com.whoiszxl.core.entity.Currency;
import com.whoiszxl.core.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    /**
     * 通过货币名称查找货币信息
     * @param currencyName 货币名称
     * @return 货币信息
     */
    public Currency findCurrency(String currencyName) {
        return currencyRepository.findCurrencyByCurrencyName(currencyName);
    }

}
