package com.whoiszxl.bitcoin.task;

import com.whoiszxl.core.entity.Currency;
import com.whoiszxl.core.entity.Height;
import com.whoiszxl.core.entity.Recharge;
import com.whoiszxl.core.enums.UpchainStatusEnum;
import com.whoiszxl.core.service.CurrencyService;
import com.whoiszxl.core.service.RechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ScanTask {

    @Value("${bitcoin.currencyName}")
    private String currencyName;

    @Autowired
    private BitcoindRpcClient bitcoinClient;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private CurrencyService currencyService;


    @Scheduled(fixedDelay = 5000)
    public void scanOrder() {
        //获取当前货币的配置信息
        Currency bitcoinInfo = currencyService.findCurrency(currencyName);

        //获取到当前与网络区块高度
        int networkBlockHeight = bitcoinClient.getBlockCount();
        Height heightObj = rechargeService.getCurrentHeight(currencyName);
        Integer currentHeight = heightObj.getCurrentHeight();

        //相隔1个区块不进行扫描
        if(networkBlockHeight - currentHeight <= 1) {
            log.info("不存在需要扫描的区块");
            return;
        }

        //扫描区块
        for(Integer i = currentHeight + 1; i <= networkBlockHeight; i++) {
            //通过区块高度拿到区块Hash，再通过区块Hash拿到区块对象，再从区块对象中拿到交易ID集合
            String blockHash = bitcoinClient.getBlockHash(i);
            BitcoindRpcClient.Block block = bitcoinClient.getBlock(blockHash);
            List<String> txs = block.tx();

            //遍历区块中的所有交易，判断是否在咱们的数据库中
            for (String txId : txs) {
                //通过交易ID获取到交易对象，从交易对象中拿到交易输出，交易输出就是交易的收款方信息。
                BitcoindRpcClient.RawTransaction transaction = bitcoinClient.getRawTransaction(txId);
                List<BitcoindRpcClient.RawTransaction.Out> outs = transaction.vOut();

                //判断交易输出集是否有效
                if(CollectionUtils.isEmpty(outs)) {
                    continue;
                }

                //遍历交易输出集
                for (BitcoindRpcClient.RawTransaction.Out out : outs) {
                    //判断公钥脚本是否有效
                    if(out.scriptPubKey() == null) {
                        continue;
                    }

                    //拿到地址在数据库判断记录是否存在
                    String address = out.scriptPubKey().addresses().get(0);
                    Recharge recharge = rechargeService.getRecharge(address, currencyName, out.value());
                    if(recharge == null) {
                        continue;
                    }

                    //更新recharge表
                    recharge.setTxHash(txId);
                    recharge.setHeight(i);
                    recharge.setUpdatedAt(new Date());
                    recharge.setUpchainAt(block.time());
                    if(block.confirmations() >= bitcoinInfo.getConfirms()) {
                        recharge.setUpchainStatus(UpchainStatusEnum.SUCCESS.getCode());
                    }else {
                        recharge.setUpchainStatus(UpchainStatusEnum.WAITING_CONFIRM.getCode());
                    }
                    rechargeService.updateRecharge(recharge);
                }

            }

        }

        //更新区块高度
        heightObj.setCurrentHeight(networkBlockHeight);
        heightObj.setUpdatedAt(new Date());
        rechargeService.updateCurrentHeight(heightObj);

    }
}
