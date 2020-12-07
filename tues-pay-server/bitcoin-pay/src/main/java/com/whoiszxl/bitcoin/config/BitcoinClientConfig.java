package com.whoiszxl.bitcoin.config;

import org.springframework.context.annotation.Configuration;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.net.MalformedURLException;

@Configuration
public class BitcoinClientConfig {


    public BitcoindRpcClient bitcoindRpcClient() {
        try {
            return new BitcoinJSONRPCClient("http://admin1:123@118.126.92.128:19001/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
