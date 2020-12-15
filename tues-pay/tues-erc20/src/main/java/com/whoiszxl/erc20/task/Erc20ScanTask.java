package com.whoiszxl.erc20.task;

import com.whoiszxl.core.entity.Currency;
import com.whoiszxl.core.entity.Height;
import com.whoiszxl.core.service.CurrencyService;
import com.whoiszxl.core.service.RechargeService;
import com.whoiszxl.core.utils.AssertUtils;
import com.whoiszxl.ethereum.service.EthereumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class Erc20ScanTask {

    @Value("${ethereum.currencyName}")
    private String currencyName;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private EthereumService ethereumService;

    @Autowired
    private RechargeService rechargeService;

    /**
     * 扫描链上的交易是否和数据库中的充值单是否匹配，如果匹配则修改对应状态。
     * 在最近的300个区块的出块时间一般平均为15秒。
     * 定时任务使用10秒间隔（10 * 1000）。
     * https://txstreet.com/
     */
    @Scheduled(fixedDelay = 10 * 1000)
    public void scanOrder() {
        //获取当前货币的配置信息
        Currency tokenInfo = currencyService.findCurrency(currencyName);
        AssertUtils.isNotNull(tokenInfo, "数据库未配置货币信息：" + currencyName);

        //获取到当前与网络区块高度
        Long networkBlockHeight = ethereumService.getBlockchainHeight();
        Height heightObj = rechargeService.getCurrentHeight(currencyName);
        if(heightObj == null) {
            Height height = new Height();
            height.setCurrencyId(tokenInfo.getId());
            height.setCurrencyName(tokenInfo.getCurrencyName());
            height.setCurrentHeight(networkBlockHeight.intValue());
            height.setUpdatedAt(new Date());
            rechargeService.saveCurrentHeight(height);
            return;
        }

        Integer currentHeight = heightObj.getCurrentHeight();

        //相隔1个区块不进行扫描
        AssertUtils.isFalse(networkBlockHeight - currentHeight <= 1, "不存在需要扫描的区块");

        //扫描区块中的交易
        for(Integer i = currentHeight + 1; i <= networkBlockHeight; i++) {
            //通过区块高度获取到区块中的交易信息
            EthBlock.Block block = ethereumService.getBlockByNumber(i.longValue());
            List<EthBlock.TransactionResult> transactionResults = block.getTransactions();
            EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResults;
            Transaction transaction = transactionObject.get();

            //通过交易Hash获取到交易的回执信息
            TransactionReceipt txReceipt = ethereumService.getTransactionReceipt(transaction.getHash());

            //判断状态是否是成功(1成功 0失败)
            if(txReceipt.getStatus().equalsIgnoreCase("0x1")) {
                //判断是否是假充值
                boolean flag = ethereumService.checkEventLog(i, tokenInfo.getContractAddress(), transaction.getHash());
                if(!flag) {
                    continue;
                }

                String input = transaction.getInput();
                String toContractAddress = transaction.getTo();
                if(!StringUtils.isEmpty(input) && input.length() >= 138 && tokenInfo.getContractAddress().equalsIgnoreCase(toContractAddress)) {
                    //TODO
                }


            }

        }
    }


    /**
     * 确认交易，将数据库中状态为待确认的充值单再次去链上查询是否确认数超过了配置确认数。
     * 在最近的300个区块的出块时间一般平均为15秒。
     * 定时任务使用15秒间隔（15 * 1000）。
     * https://txstreet.com/
     */
    @Scheduled(fixedDelay = 10 * 1000)
    public void confirmTx() {

    }
}
