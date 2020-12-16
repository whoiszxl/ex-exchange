## Tues Pay
一个应用在中心化系统的加密货币支付Java SDK，暂实现比特币支付、以太币支付与ERC20 token支付。你可以很方便的通过文档快速为电商、金融等业务接入加密货币的充值提现、购买商品等等。


## 项目结构

tues-pay
├── tues-bitcoin          -- 比特币Java服务
├── tues-core             -- 加密货币Java服务核心依赖
├── tues-erc20            -- ERC20代币Java服务
├── tues-eth              -- 以太币Java服务
└── tues-ethereum-core    -- 以太坊Java服务核心依赖

docs                      -- 环境搭建文档
sql                       -- 数据库脚本


## 工作原理
项目使用Java语言构建，通过定时任务对我们搭建的加密货币节点进行扫描，以此来进行充值操作。接入后，可以在生成订单的时候调用tues-pay接口（`/currencyName/createRecharge/{orderId}/{amount}`），选择对应的`currencyName`并传入订单ID与金额则可生成一笔充值单，生成充值单的过程中会触发钱包地址生成，并与订单形成对应。同时Task任务会从区块高度为0扫描到当前区块高度，将每个区块中的交易进行遍历，当匹配到充值地址与金额在数据库中有对应时，则充值单得到确认。同时，会有另一个确认订单的任务在同步运行，循环遍历当前数据库中的`已确认充值单`，当交易所在区块的确认数已经达到系统配置中的数量，则充值成功。

![工作原理图](http://hexo.whoiszxl.com/tues-pay.jpg)
![工作原理图](http://hexo.whoiszxl.com/tues-pay2.jpg)


## 项目部署
1. 通过docs目录中的环境搭建文档搭建好钱包节点。

2. 通过`tues-core`中的`EncryptPropertiesUtils工具类`将节点的账号密码等信息进行加密处理，避免在文件中明文显示。

3. 在MySQL数据库中执行`sql/wallet.sql`创建脚本文件，并将数据库连接URL与账号密码使用`EncryptPropertiesUtils工具类`进行加密处理，避免在文件中明文显示。

4. 将tues-pay通过maven打包，然后`java -jar xxx.jar`运行。