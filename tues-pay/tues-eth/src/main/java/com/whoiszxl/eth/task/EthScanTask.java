package com.whoiszxl.eth.task;

import com.whoiszxl.core.entity.Currency;
import com.whoiszxl.core.entity.Height;
import com.whoiszxl.core.entity.Recharge;
import com.whoiszxl.core.enums.UpchainStatusEnum;
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
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class EthScanTask {


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
        Currency ethInfo = currencyService.findCurrency(currencyName);
        AssertUtils.isNotNull(ethInfo, "数据库未配置货币信息：" + currencyName);

        //获取到当前与网络区块高度
        Long networkBlockHeight = ethereumService.getBlockchainHeight();
        Height heightObj = rechargeService.getCurrentHeight(currencyName);
        if(heightObj == null) {
            Height height = new Height();
            height.setCurrencyId(ethInfo.getId());
            height.setCurrencyName(ethInfo.getCurrencyName());
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
            EthBlock.Block block = ethereumService.getBlockByNumber(i.longValue());

            List<EthBlock.TransactionResult> transactions = block.getTransactions();

            for (EthBlock.TransactionResult transactionResult : transactions) {
                EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
                Transaction transaction = transactionObject.get();

                if(StringUtils.isEmpty(transaction.getTo())) {
                    log.info("交易{}不存在toAddress", transaction.getHash());
                    continue;
                }

                BigDecimal amount = Convert.fromWei(transaction.getValue().toString(), Convert.Unit.ETHER);
                Recharge recharge = rechargeService.getRecharge(transaction.getTo(), currencyName, amount);
                if(recharge == null) {
                    log.info("地址不在库中：{}", transaction.getTo());
                    continue;
                }

                recharge.setFromAddress(transaction.getFrom());
                recharge.setTxHash(transaction.getHash());
                recharge.setCurrentConfirm(transaction.getBlockNumber().subtract(BigInteger.valueOf(i)).intValue());
                recharge.setHeight(transaction.getBlockNumber().intValue());
                recharge.setUpchainAt(new Date(block.getTimestamp().longValue()));
                recharge.setUpdatedAt(new Date());

                if(i - block.getNumber().intValue() >= ethInfo.getConfirms()) {
                    recharge.setUpchainStatus(UpchainStatusEnum.SUCCESS.getCode());
                    recharge.setUpchainSuccessAt(new Date(block.getTimestamp().longValue()));
                }else {
                    recharge.setUpchainStatus(UpchainStatusEnum.WAITING_CONFIRM.getCode());
                }
                rechargeService.updateRecharge(recharge);
            }

        }

        //更新区块高度
        heightObj.setCurrentHeight(networkBlockHeight.intValue());
        heightObj.setUpdatedAt(new Date());
        rechargeService.saveCurrentHeight(heightObj);

    }



    /**
     * 确认交易，将数据库中状态为待确认的充值单再次去链上查询是否确认数超过了配置确认数。
     * 在最近的300个区块的出块时间一般平均为15秒。
     * 定时任务使用15秒间隔（15 * 1000）。
     * https://txstreet.com/
     */
    @Scheduled(fixedDelay = 10 * 1000)
    public void confirmTx() {
        //0. 获取当前货币的配置信息
        Currency ethInfo = currencyService.findCurrency(currencyName);
        AssertUtils.isNotNull(ethInfo, "数据库未配置货币信息：" + currencyName);

        //1. 获取当前网络的区块高度
        Long currentHeight = ethereumService.getBlockchainHeight();

        //2. 查询到所有待确认的充值单
        List<Recharge> waitConfirmRecharge = rechargeService.getWaitConfirmRecharge(currencyName);
        AssertUtils.isNotNull(waitConfirmRecharge, "不存在待确认的充值单");

        //3. 遍历库中交易进行判断是否成功
        for (Recharge recharge : waitConfirmRecharge) {
            Transaction transaction = ethereumService.getTransactionByHash(recharge.getTxHash());

            //如果链上交易确认数大于等于配置的确认数，则更新充值单为成功并更新上链成功时间，否则只更新当前确认数。
            if(currentHeight - transaction.getBlockNumber().longValue()  >= ethInfo.getConfirms()) {
                recharge.setUpchainStatus(UpchainStatusEnum.SUCCESS.getCode());
                recharge.setUpchainSuccessAt(new Date());
            }
            recharge.setCurrentConfirm((int) (currentHeight - transaction.getBlockNumber().longValue()));
            recharge.setUpdatedAt(new Date());

            rechargeService.saveRecharge(recharge);
        }
    }

}
