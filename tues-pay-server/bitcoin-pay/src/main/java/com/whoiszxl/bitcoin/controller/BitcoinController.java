package com.whoiszxl.bitcoin.controller;

import com.whoiszxl.bitcoin.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/getBalanceByAddress/{address}")
    public Result<String> getAddressBalance(@PathVariable String address) {
        String account = bitcoinClient.getAccount(address);
        BigDecimal balance = bitcoinClient.getBalance(account);
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
