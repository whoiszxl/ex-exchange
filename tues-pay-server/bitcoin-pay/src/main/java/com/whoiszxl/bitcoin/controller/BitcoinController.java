package com.whoiszxl.bitcoin.controller;

import com.whoiszxl.bitcoin.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/bitcoin")
public class BitcoinController {

    private BitcoindRpcClient bitcoinClient;
    @PostConstruct
    public void init() {
        try {
            bitcoinClient = new BitcoinJSONRPCClient("http://admin1:123@118.126.92.128:19001/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/getAllBalance")
    public Result<String> getAllBalance() {
        BigDecimal balance = bitcoinClient.getBalance();
        return Result.buildSuccess(balance.toPlainString());
    }

}
