package com.iot.dtm.relay;

import com.iot.dtm.config.MqConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上行转发：消费 ProxyTcp 上行（proxy.uplink），转发到后台（platform.uplink）。
 * 纯透传，不解析业务内容；失败由 retry + 死信保障可靠性。
 */
@Component
public class UplinkRelay {

    private static final Logger log = LoggerFactory.getLogger(UplinkRelay.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MqConstants.PROXY_UPLINK)
    public void onUplink(Message message) {
        int size = message.getBody().length;
        log.info("dtm uplink relay: {} bytes -> {}", size, MqConstants.PLATFORM_UPLINK);
        rabbitTemplate.send(MqConstants.EXCHANGE, MqConstants.PLATFORM_UPLINK, message);
    }
}
