package org.joq4j.rabbitmq.amqp;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.common.net.ConnectionUrl;
import org.joq4j.common.utils.Strings;
import org.joq4j.config.Config;
import org.joq4j.config.ConfigDescriptor;
import org.joq4j.config.ConfigHelper;
import org.joq4j.config.Configurable;

@Accessors(chain = true, fluent = true)
@Data
@Configurable
public class RabMQConfig {
    @ConfigDescriptor(name = "rabmq.url")
    @Getter(AccessLevel.NONE)
    private String url;

    @ConfigDescriptor(name = "rabmq.host", defaultValue = "localhost")
    private String host;

    @ConfigDescriptor(name = "rabmq.port", defaultValue = "5672")
    private int port;

    @ConfigDescriptor(name = "rabmq.username")
    private String username;

    @ConfigDescriptor(name = "rabmq.password")
    private String password;

    @ConfigDescriptor(name = "rabmq.virtual.host", defaultValue = "/")
    private String virtualHost;

    @ConfigDescriptor(name = "rabmq.queue.name", defaultValue = "jo4j-rabbitmq-queue")
    private String queueName;

    @ConfigDescriptor(name = "rabmq.queue.durable", defaultValue = "true")
    private boolean queueDurable;

    @ConfigDescriptor(name = "rabmq.queue.autodel", defaultValue = "false")
    private boolean queueAutoDel;

    @ConfigDescriptor(name = "rabmq.exchange.name", defaultValue = "joq4j-rabbitmq-exchange")
    private String exchangeName;

    @ConfigDescriptor(name = "rabmq.exchange.type", defaultValue = "direct")
    private String exchangeType;

    @ConfigDescriptor(name = "rabmq.routing.key", defaultValue = "joq4j-rabbitmq-routing-key")
    private String routingKey;


    public RabMQConfig(Config config, String url){
        this(config);
        this.url = url;
    }

    public RabMQConfig(Config config){
        ConfigHelper.injectFields(this, config);
    }

    public String url(boolean withCredentials) {
        if (Strings.isNonEmpty(this.url)) {
            return this.url;
        }
        ConnectionUrl connectionUrl = new ConnectionUrl()
                .scheme("amqp")
                .host(this.host)
                .port(this.port)
                .username(this.username)
                .virtualHost(this.virtualHost);
        return connectionUrl.asString(withCredentials);
    }
}
