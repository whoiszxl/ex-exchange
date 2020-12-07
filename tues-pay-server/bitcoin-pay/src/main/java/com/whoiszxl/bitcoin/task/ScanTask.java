package com.whoiszxl.bitcoin.task;

import com.whoiszxl.core.service.RechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;

@Slf4j
@Component
public class ScanTask {

    @Autowired
    private BitcoindRpcClient bitcoinClient;

    @Autowired
    private RechargeService orderService;


    @Scheduled(fixedDelay = 5000)
    public void scanOrder() {
        //获取区块高度
        int blockCount = bitcoinClient.getBlockCount();
    }
}
