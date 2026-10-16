package com.iot.dtm.config;

/**
 * RabbitMQ 队列/交换器命名常量。
 * 与 design/api-design.md 中的组件间 MQ 契约保持一致。
 */
public final class MqConstants {

    public static final String EXCHANGE = "iot.exchange";
    public static final String DLX = "iot.dlx";
    public static final String DLQ = "iot.dlq";

    /** 网关（ProxyTcp）上行入口：dtm 从此消费，转发到后台 */
    public static final String PROXY_UPLINK = "proxy.uplink";
    /** 后台（platform_java）上行入口：dtm 转发到此，后台消费 */
    public static final String PLATFORM_UPLINK = "platform.uplink";
    /** 后台下行入口：dtm 从此消费，转发到网关 */
    public static final String PLATFORM_DOWNLINK = "platform.downlink";
    /** 网关下行入口：dtm 转发到此，网关消费 */
    public static final String PROXY_DOWNLINK = "proxy.downlink";

    private MqConstants() {
    }
}
