package com.whoiszxl.bitcoin.controller;

import com.whoiszxl.core.common.Result;
import com.whoiszxl.core.common.response.RechargeResponse;
import com.whoiszxl.core.entity.Currency;
import com.whoiszxl.core.entity.Recharge;
import com.whoiszxl.core.enums.UpchainStatusEnum;
import com.whoiszxl.core.service.CurrencyService;
import com.whoiszxl.core.service.RechargeService;
import com.whoiszxl.core.utils.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/bitcoin")
public class BitcoinController {

    @Autowired
    private BitcoindRpcClient bitcoinClient;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private CurrencyService currencyService;

    @Value("${bitcoin.currencyName}")
    private String currencyName;

    /**
     * 创建一个充值单，通过业务系统传入相关联的订单号与金额入库到支付系统中，并会生成一个相应的货币地址
     * 支付系统通过扫描所有区块中的交易，一旦通过地址和金额匹配上就执行充值业务
     * @param orderId 业务系统订单ID
     * @param amount 业务系统订单金额
     * @return
     */
    @GetMapping("/createRecharge/{orderId}/{amount}")
    public Result<RechargeResponse> createRechargeRecord(@PathVariable String orderId, @PathVariable String amount) {
        //数据有效性校验
        AssertUtils.hasText(orderId, "订单号不能为空");
        AssertUtils.hasText(amount, "金额不能为空");
        AssertUtils.isDouble(amount, "金额格式错误");

        Recharge checkRecharge = rechargeService.getRechargeByOrderId(currencyName, orderId);
        if(checkRecharge == null) {
            return Result.buildError("充值单记录已存在");
        }

        //获取货币信息
        Currency bitcoinInfo = currencyService.findCurrency(currencyName);
        AssertUtils.isNotNull(bitcoinInfo, "数据库未配置货币信息：" + currencyName);

        //构建充值单信息
        Recharge recharge = new Recharge();
        recharge.setOrderId(orderId);
        recharge.setAmount(new BigDecimal(amount));
        recharge.setCurrencyId(bitcoinInfo.getId());
        recharge.setCurrencyName(bitcoinInfo.getCurrencyName());
        recharge.setUpchainStatus(UpchainStatusEnum.NOT_UPCHAIN.getCode());
        Date currentDate = new Date();
        recharge.setUpdatedAt(currentDate);
        recharge.setCreatedAt(currentDate);

        //通过orderId为账号创建一个bitcoin地址
        String newAddress = bitcoinClient.getNewAddress(orderId);
        recharge.setToAddress(newAddress);

        //落库
        rechargeService.saveRecharge(recharge);

        //二维码数据拼接
        String qrcodeData = newAddress + "?amount=" + amount + "&label=" + orderId;

        return Result.buildSuccess(new RechargeResponse(newAddress, qrcodeData));
    }


    @GetMapping("/getNewAddress/{account}")
    public Result<String> getNewAddress(@PathVariable String account) {
        String newAddress = bitcoinClient.getNewAddress(account);
        return Result.buildSuccess(newAddress);
    }

    @GetMapping("/getAllBalance")
    public Result<String> getAllBalance() {
        BigDecimal balance = bitcoinClient.getBalance();
        return Result.buildSuccess(balance.toPlainString());
    }

    @GetMapping("/getHeight")
    public Result<String> getHeight() {
        int blockCount = bitcoinClient.getBlockCount();
        return Result.buildSuccess(blockCount + "");
    }

    @GetMapping("/getBlockChainInfo")
    public Result<BitcoindRpcClient.BlockChainInfo> getBlockChainInfo() {
        BitcoindRpcClient.BlockChainInfo blockChainInfo = bitcoinClient.getBlockChainInfo();
        return Result.buildSuccess(blockChainInfo);
    }
}
