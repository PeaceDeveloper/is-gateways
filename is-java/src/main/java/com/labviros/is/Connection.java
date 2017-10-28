package com.labviros.is;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * Created by picoreti on 07/04/17.
 */
public class Connection implements Serializable {
    private final static Logger log = LogManager.getLogger(Connection.class.getName());

    private final com.rabbitmq.client.ConnectionFactory factory;
    private final com.rabbitmq.client.Connection connection;
    private final com.rabbitmq.client.Channel channel;
    private final String dataExchange = "data";
    private final String serviceExchange = "services";

    private final Router router;
    private final ServiceClient client;

    public final void assertExchange(String name) throws IOException {
        boolean passive = false;
        boolean durable = false;
        boolean autoDelete = false;
        channel.exchangeDeclare(name, BuiltinExchangeType.TOPIC, passive, durable, autoDelete, null);
    }

    public Connection(String uri) throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException, TimeoutException, IOException {
        factory = new ConnectionFactory();
        factory.setConnectionTimeout(5000);
        factory.setUri(uri);
        connection = factory.newConnection();
        channel = connection.createChannel();

        assertExchange(this.getDataExchange());
        assertExchange(this.getServiceExchange());

        router = new Router(channel);

        ArrayBlockingQueue<Message> replies = subscribe("services", router.getQueue());
        client = new ServiceClient(channel, replies, router.getQueue());

        log.info("Connection successful uri: " + uri);
    }

    public final void stop() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public final ArrayBlockingQueue<Message> subscribe(String exchange, String topic) throws IOException {
        channel.queueBind(router.getQueue(), exchange, topic);
        return router.addRoute(exchange, topic);
    }

    public final void unsubscribe(String exchange, String topic) throws IOException {
        channel.queueUnbind(router.getQueue(), exchange, topic);
        router.removeRoute(exchange, topic);
    }

    public final void publish(String exchange, String topic, Message message) throws Exception {
        message.pack();
        AMQP.BasicProperties properties = message.getPropertiesBuilder().build();
        channel.basicPublish(exchange, topic, properties, message.getBody());
    }

    public final <T> ServiceProvider<T> advertise(T impl) throws IOException {
        return new ServiceProvider(channel, impl);
    }

    public final ServiceClient client() {
        return client;
    }

	public String getDataExchange() {
		return dataExchange;
	}

	public String getServiceExchange() {
		return serviceExchange;
	}

}
