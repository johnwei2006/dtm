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
public class UplinkRelayTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UplinkRelay relay;

    @Test
    public void forwardsUplinkToPlatformQueue() {
        Message msg = new Message("hello".getBytes(), new MessageProperties());
        relay.onUplink(msg);

        ArgumentCaptor<String> exchange = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Message> body = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).send(exchange.capture(), routingKey.capture(), body.capture());

        assertEquals(MqConstants.EXCHANGE, exchange.getValue());
        assertEquals(MqConstants.PLATFORM_UPLINK, routingKey.getValue());
        assertEquals("hello", new String(body.getValue().getBody()));
    }

    @Test
    public void forwardsEmptyBodyPreservingContent() {
        Message msg = new Message(new byte[0], new MessageProperties());
        relay.onUplink(msg);
        ArgumentCaptor<Message> body = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).send(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), body.capture());
        assertEquals(0, body.getValue().getBody().length);
    }
}
