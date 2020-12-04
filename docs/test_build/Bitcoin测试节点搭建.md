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
