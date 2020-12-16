package com.whoiszxl.erc20.controller;

import com.whoiszxl.core.common.Result;
import com.whoiszxl.core.common.response.RechargeResponse;
import com.whoiszxl.core.entity.Currency;
import com.whoiszxl.core.entity.CurrencyAccount;
import com.whoiszxl.core.entity.Recharge;
import com.whoiszxl.core.enums.UpchainStatusEnum;
import com.whoiszxl.core.service.CurrencyService;
import com.whoiszxl.core.service.RechargeService;
import com.whoiszxl.core.utils.AssertUtils;
import com.whoiszxl.ethereum.common.EthereumAddress;
import com.whoiszxl.ethereum.service.EthereumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/erc20")
public class Erc20Controller {

    @Value("${ethereum.currencyName}")
    private String currencyName;

    @Autowired
    private EthereumService ethereumService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private RechargeService rechargeService;

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
        if(checkRecharge != null) {
            return Result.buildError("充值单记录已存在");
        }

        //获取货币信息
        Currency tokenInfo = currencyService.findCurrency(currencyName);
        AssertUtils.isNotNull(tokenInfo, "数据库未配置货币信息：" + currencyName);

        //构建充值单信息
        Recharge recharge = new Recharge();
        recharge.setOrderId(orderId);
        recharge.setAmount(new BigDecimal(amount));
        recharge.setCurrencyId(tokenInfo.getId());
        recharge.setCurrencyName(tokenInfo.getCurrencyName());
        recharge.setUpchainStatus(UpchainStatusEnum.NOT_UPCHAIN.getCode());
        Date currentDate = new Date();
        recharge.setUpdatedAt(currentDate);
        recharge.setCreatedAt(currentDate);

        //通过orderId为账号创建一个bitcoin地址
        EthereumAddress ethereumAddress = ethereumService.createAddressByFile();
        recharge.setToAddress(ethereumAddress.getAddress());
        //落库
        rechargeService.saveRecharge(recharge);

        //保存keystore文件与orderId的对应关系
        CurrencyAccount account = new CurrencyAccount();
        account.setAddress(ethereumAddress.getAddress());
        account.setCurrencyId(tokenInfo.getId());
        account.setCurrencyName(tokenInfo.getCurrencyName());
        account.setKeystoreName(ethereumAddress.getKeystoreName());
        account.setMnemonic(ethereumAddress.getMnemonic());
        Date now = new Date();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        rechargeService.saveAccount(account);

        //二维码数据拼接
        BigDecimal amountWei = Convert.toWei(amount, Convert.Unit.ETHER);
        String qrcodeData = "ethereum:" + ethereumAddress.getAddress() + "?value=" + amountWei.toPlainString();

        return Result.buildSuccess(new RechargeResponse(ethereumAddress.getAddress(), qrcodeData));
    }
}
