# SagaTcc 测试 Demo

本项目参考 EasyTransaction 的 Saga-TCC 示例，使用当前 `SagaTcc` 项目重新实现，包含以下三个模块：

- `sagatcc-wallet-api`：钱包服务对外发布的 SagaTcc 请求契约。
- `sagatcc-wallet-service`：钱包参与方，负责余额冻结、确认扣款和取消解冻。
- `sagatcc-order-service`：事务协调方，负责创建订单、登记钱包分支以及更新订单最终状态。

业务库和事务库位于同一个 MySQL 实例中，但使用不同 Schema：

```text
order_db       订单业务表
wallet_db      钱包业务表
saga_tcc_db    SagaTcc 事务、分支、Outbox 和参与方幂等表
```

两个服务都连接自己的业务 Schema，并通过 `sagatcc.schema=saga_tcc_db` 将 SagaTcc SQL 路由到事务 Schema。业务数据和 SagaTcc 数据仍使用同一个 `DataSource`、同一个连接和同一个本地事务。

两个服务的 Spring Boot 启动类都显式添加了 `@EnableSagaTcc`。只引入 starter 而没有该注解时，不会注册 SagaTcc Bean、RocketMQ 监听器或后台任务。

## 1. 环境要求

- JDK 8 或更高版本
- Maven 3.8 或更高版本
- MySQL 5.7 或 MySQL 8
- RocketMQ 4.x

## 2. 安装 SagaTcc

Demo 使用同级目录中的 SagaTcc：

```bash
cd ../SagaTcc
mvn clean install
```

## 3. 初始化数据库

先创建业务 Schema 和表：

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p < sql/01-business.sql
```

再创建 SagaTcc 表：

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p saga_tcc_db < sql/02-sagatcc.sql
```

Demo 默认使用 `root/root`。可以通过环境变量覆盖连接信息：

```bash
export DEMO_MYSQL_HOST=127.0.0.1
export DEMO_MYSQL_PORT=3306
export DEMO_MYSQL_USERNAME=root
export DEMO_MYSQL_PASSWORD=root
export ROCKETMQ_NAME_SERVER=127.0.0.1:9876
```

如果订单服务和钱包服务使用不同数据库账号，两个账号都必须拥有各自业务 Schema 和 `saga_tcc_db` 的 DML 权限。

## 4. 构建并启动

```bash
mvn clean package
```

先启动钱包服务：

```bash
java -jar sagatcc-wallet-service/target/sagatcc-wallet-service-1.0.0-SNAPSHOT.jar
```

再启动订单服务：

```bash
java -jar sagatcc-order-service/target/sagatcc-order-service-1.0.0-SNAPSHOT.jar
```

钱包服务端口为 `8081`，订单服务端口为 `8080`。

## 5. 验证成功流程

初始用户 `1` 的余额为 `10000`。

```bash
curl -X POST "http://localhost:8080/orders?userId=1&amount=100"
```

接口会返回 `orderId` 和 `sagaId`。等待异步事务完成后查询订单和钱包：

```bash
curl "http://localhost:8080/orders/1"
curl "http://localhost:8081/wallets/1"
```

预期订单状态最终变为 `PAID`，钱包总余额减少 `100`，冻结余额恢复为 `0`。

## 6. 验证取消流程

提交一个超过可用余额的订单：

```bash
curl -X POST "http://localhost:8080/orders?userId=1&amount=20000"
```

钱包 Try 阶段会返回不可重试失败，SagaTcc 随后执行 Cancel，订单状态最终变为 `CANCELLED`，钱包余额不变。

## 7. 路由说明

`WalletPayRequest` 位于 `sagatcc-wallet-api` 模块：

```java
@SagaTccBusiness(appId = "wallet-service", busCode = "walletPay")
public class WalletPayRequest implements SagaTccRequest {
}
```

订单服务调用 `sagaTccOperations.enlist(request)` 时，会根据 `appId + busCode` 将命令发送给钱包服务。钱包服务的 `spring.application.name` 必须与 `appId` 保持一致。
