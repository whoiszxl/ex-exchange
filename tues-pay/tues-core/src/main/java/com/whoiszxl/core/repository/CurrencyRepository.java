package com.whoiszxl.core.repository;

import com.whoiszxl.core.entity.Currency;


public interface CurrencyRepository extends BaseRepository<Currency>{

    /**
     * 通过货币名称查找货币信息
     * @param currencyName 货币名称
     * @return 货币信息
     */
    Currency findCurrencyByCurrencyName(String currencyName);
}
