package com.whoiszxl.bitcoin.controller;

import com.whoiszxl.core.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bitcoin")
public class BitcoinController {

    @Autowired
    private BitcoindRpcClient bitcoinClient;

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
