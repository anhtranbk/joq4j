package org.joq4j.rabbitmq.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class RabMQConnections {
    public static synchronized Channel getChannel(RabMQConfig config){
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(config.url(true));
            Connection connection = factory.newConnection();
            Channel channel =  connection.createChannel();
            channel.queueDeclare(config.queueName(), config.queueDurable(), true, config.queueAutoDel(), null);
            channel.exchangeDeclare(config.exchangeName(), config.exchangeType());
            channel.queueBind(config.queueName(), config.exchangeName(), config.routingKey());
            return channel;
        } catch (URISyntaxException
                | NoSuchAlgorithmException
                | KeyManagementException
                | IOException
                | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
