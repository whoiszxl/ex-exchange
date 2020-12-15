## infura构建以太坊全环境

1. 打开官网：`https://infura.io/`，进行账号注册。

2. 登录后进入仪表盘，选择`Ethereum`,再点击`create new project`创建一个项目。

3. 创建项目后进入项目详情，在`settings`中能查看到项目的ID与SECRET。

4. 复制请求地址进行测试
```sh
curl -X POST \
  https://rinkeby.infura.io/v3/project_id \
  -H 'content-type: application/json' \
  -H 'postman-token: 42c8b67a-5c74-f38e-8376-33ce2918606e' \
  -d '{
	"jsonrpc":"2.0",
	"method":"eth_getBalance",
	"params":["0x36d429596a4DA28CD93d8CF3dE5e2557f27dA2A5", "latest"],
	"id":1
}'
```

5. infura可以修改二级域名的名称进行网络间的切换， 其支持：`mainnet, rposten, kovan, rinkeby, goerli`