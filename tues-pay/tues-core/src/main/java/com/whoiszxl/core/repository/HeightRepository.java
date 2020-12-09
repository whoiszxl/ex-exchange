package com.whoiszxl.core.repository;

import com.whoiszxl.core.entity.Height;

public interface HeightRepository extends BaseRepository<Height>{

    /**
     * 通过区块高度
     * @param currencyName 货币名称
     * @return 货币区块高度
     */
    Height getHeightByCurrencyName(String currencyName);

}
