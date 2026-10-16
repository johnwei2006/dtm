package com.iot.dtm.relay;

import com.iot.dtm.config.MqConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DownlinkRelayTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DownlinkRelay relay;

    @Test
    public void forwardsDownlinkToProxyQueue() {
        Message msg = new Message("cmd".getBytes(), new MessageProperties());
        relay.onDownlink(msg);

        ArgumentCaptor<String> exchange = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Message> body = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).send(exchange.capture(), routingKey.capture(), body.capture());

        assertEquals(MqConstants.EXCHANGE, exchange.getValue());
        assertEquals(MqConstants.PROXY_DOWNLINK, routingKey.getValue());
        assertEquals("cmd", new String(body.getValue().getBody()));
    }
}
