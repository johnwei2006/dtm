# dtm — IoT 数据传输服务

数据传输服务（Data Transfer Module），位于 `ProxyTcp` 与 `platform_java` 之间，负责上下行消息的**可靠转发、削峰与解耦**。

> 设计基线见文档仓 [`12_design/dtm/design.md`](https://github.com/johnwei2006/iot-platform-docs/blob/main/12_design/dtm/design.md) 与 [`12_design/api-design.md`](https://github.com/johnwei2006/iot-platform-docs/blob/main/12_design/api-design.md)。

## 职责

- **上行转发**：消费 `proxy.uplink`（来自网关）→ 转发到 `platform.uplink`（后台）。
- **下行转发**：消费 `platform.downlink`（来自后台）→ 转发到 `proxy.downlink`（网关）。
- **可靠性**：消费失败自动重试（最多 3 次），超限进入死信队列 `iot.dlq`，可重放。
- **解耦**：接入层与业务层故障互不致命。

## 技术栈

- Spring Boot 2.1.6（与 `ProxyTcp` 对齐，Java 8）
- Spring AMQP（RabbitMQ）
- Actuator（健康检查）

## 消息契约（RabbitMQ）

| 队列 | 方向 | 说明 |
| --- | --- | --- |
| `proxy.uplink` | 网关 → dtm | 设备上行报文（原始字节透传） |
| `platform.uplink` | dtm → 后台 | 同上行，后台消费 |
| `platform.downlink` | 后台 → dtm | 下行指令（原始字节透传） |
| `proxy.downlink` | dtm → 网关 | 同下行，网关消费 |
| `iot.dlq` | 死信 | 处理失败的消息 |

交换器：`iot.exchange`（topic）；死信交换器：`iot.dlx`。

## 构建与运行

```bash
mvn clean package
java -jar target/dtm-0.0.1-SNAPSHOT.jar
# 或开发运行
mvn spring-boot:run
```

- 端口：`8082`，健康检查：`GET /health`、`/actuator/health`。
- RabbitMQ 连接可通过环境变量覆盖：`RABBITMQ_HOST` / `RABBITMQ_PORT` / `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD`。

## 可观测

- 日志：`com.iot.dtm` 打印每次转发的字节数与目标队列。
- 死信：监控 `iot.dlq` 积压以发现异常消息。
