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
 * 下行转发：消费后台下行（platform.downlink），转发到网关（proxy.downlink）。
 * 纯透传，不解析业务内容；失败由 retry + 死信保障可靠性。
 */
@Component
public class DownlinkRelay {

    private static final Logger log = LoggerFactory.getLogger(DownlinkRelay.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MqConstants.PLATFORM_DOWNLINK)
    public void onDownlink(Message message) {
        int size = message.getBody().length;
        log.info("dtm downlink relay: {} bytes -> {}", size, MqConstants.PROXY_DOWNLINK);
        rabbitTemplate.send(MqConstants.EXCHANGE, MqConstants.PROXY_DOWNLINK, message);
    }
}
