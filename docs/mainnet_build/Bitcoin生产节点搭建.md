## Bitcoin生产节点搭建

> 官方文档：https://bitcoin.org/en/full-node

### Linux构建全节点
1. 在(Bitcoin官网)[https://bitcoin.org/en/download]下载`Linux(tgz) bitcoin-0.20.1-x86_64-linux-gnu.tar.gz`文件

2. 将文件进行解压：`tar -zxvf bitcoin-0.20.1-x86_64-linux-gnu.tar.gz`。

3. 修改配置文件，默认的配置文件在`~/.bitcoin/bitcoin.conf`，可以通过在命令行运行时指定自定义文件`./bitcoind --conf=/opt/module/bitcoin/bitcoin.conf`。

```conf
# 指定区块数据存储目录，所在目录需要比较大的磁盘空间
datadir=/opt/module/bitcoin/data

# 后台运行
daemon=1

# 添加连接节点
addnode=69.164.218.197
addnode=10.0.0.2:8333

# 添加连接节点（仅连接，保证隐私）
connect=69.164.218.197
connect=10.0.0.1:8333

# 监听连接端口 (default: 8333, testnet: 18333, regtest: 18444)
port=8333

# 入站与出站最大连接数
maxconnections=5

# server=1可接收json-rpc请求，server=0不可
server=0

# rpc请求的账号与密码
rpcuser=alice
rpcpassword=yourRpcPassword

# 一个http请求的超时时间（秒）
rpcclienttimeout=30

# 默认只允许本机连接，通过rpcallowip可允许多个机器进行请求
rpcallowip=10.1.1.34/255.255.255.0
rpcallowip=1.2.3.4/24
rpcallowip=2001:db8:85a3:0:0:8a2e:370:7334/96

# RPC端口
rpcport=8332

# 配置rpcconnect可以连接到其他机器的节点
rpcconnect=127.0.0.1

# 额外的交易手续费
paytxfee=0.000

# 预生成密钥对数量
keypool=100
```

4. 配置完成后，则可以启动bitcoin，输出的日志中progress=1.0的时候则同步完成。
```sh
cd bitcoin/bin
./bitcoind
```

5. 执行bitcoin-cli可以对节点进行操作
```sh
# 查看区块链信息
bitcoin-cli -rpcuser=bitcoin -rpcpassword=bitcoin getblockchaininfo
```