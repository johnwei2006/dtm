package com.iot.dtm.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 声明 dtm 所需的队列、交换器与绑定。
 * 所有业务队列均配置死信（x-dead-letter），消费失败重试超限后进入死信队列。
 */
@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange iotExchange() {
        return new TopicExchange(MqConstants.EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(MqConstants.DLX, true, false);
    }

    private Queue durableQueueWithDlq(String name) {
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", MqConstants.DLX)
                .withArgument("x-dead-letter-routing-key", MqConstants.DLQ)
                .build();
    }

    @Bean
    public Queue proxyUplinkQueue() {
        return durableQueueWithDlq(MqConstants.PROXY_UPLINK);
    }

    @Bean
    public Queue platformUplinkQueue() {
        return durableQueueWithDlq(MqConstants.PLATFORM_UPLINK);
    }

    @Bean
    public Queue platformDownlinkQueue() {
        return durableQueueWithDlq(MqConstants.PLATFORM_DOWNLINK);
    }

    @Bean
    public Queue proxyDownlinkQueue() {
        return durableQueueWithDlq(MqConstants.PROXY_DOWNLINK);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(MqConstants.DLQ, true);
    }

    @Bean
    public Binding bindProxyUplink() {
        return new Binding(MqConstants.PROXY_UPLINK, Binding.DestinationType.QUEUE, MqConstants.EXCHANGE, MqConstants.PROXY_UPLINK, null);
    }

    @Bean
    public Binding bindPlatformUplink() {
        return new Binding(MqConstants.PLATFORM_UPLINK, Binding.DestinationType.QUEUE, MqConstants.EXCHANGE, MqConstants.PLATFORM_UPLINK, null);
    }

    @Bean
    public Binding bindPlatformDownlink() {
        return new Binding(MqConstants.PLATFORM_DOWNLINK, Binding.DestinationType.QUEUE, MqConstants.EXCHANGE, MqConstants.PLATFORM_DOWNLINK, null);
    }

    @Bean
    public Binding bindProxyDownlink() {
        return new Binding(MqConstants.PROXY_DOWNLINK, Binding.DestinationType.QUEUE, MqConstants.EXCHANGE, MqConstants.PROXY_DOWNLINK, null);
    }

    @Bean
    public Binding bindDeadLetter() {
        return new Binding(MqConstants.DLQ, Binding.DestinationType.QUEUE, MqConstants.DLX, MqConstants.DLQ, null);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
