## Bitcoin测试节点搭建

### Docker方式
1. 拉取Docker镜像
```
docker pull freewil/bitcoin-testnet-box
```

2. 运行Docker镜像并进入环境

此命令同同时启动两个Bitcoin测试节点，组成一个最简单的比特币网络。
```
docker run -it -p 19001:19001 -p 19011:19011 freewil/bitcoin-testnet-box
```

3. 启动比特币网络
```
make start
```

4. 查看节点信息
```
make getinfo
```

5. 打开外网访问

编辑文件/home/tester/bitcoin-testnet-box/1/bitcoin.conf
```sh
将
#rpcallowip=0.0.0.0/0
#rpcallowip=::/0

修改为
rpcallowip=0.0.0.0/0
rpcallowip=::/0

并重启
make stop && make start
```

6. 使用如下CURL命令进行测试
```sh

Basic 通过在bitcoin.conf中配置的rpcuser，rpcpassword生成

curl -X POST \
  http://your-ip-address:19001/ \
  -H 'authorization: Basic YWRtaW4xOjEyMw==' \
  -H 'content-type: application/json' \
  -d '{
	"method": "getblockchaininfo"
}'
```

7. 初始化测试区块链数据

```sh
# 生成地址
bitcoin-cli -datadir=1 getnewaddress

# 生成区块,可以指定生成的区块数，生成区块所得的coinbase奖励必须在100个块以后才能使用
bitcoin-cli -datadir=1 generate 100

```

8. 发送交易
```sh
bitcoin-cli -datadir=2 sendtoaddress 2NFaZceBAGwa2QeMk1HVKsLXP6yJ2tnZhB4 1.99
```


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