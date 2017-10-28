package com.labviros.is;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by picoreti on 07/04/17.
 */
public class Router extends DefaultConsumer {
    private final static Logger log = LogManager.getLogger(Router.class.getName());

    private String queue;
    private Map<String, ArrayBlockingQueue<Message>> routes = new HashMap<>();

    public String getQueue() {
        return queue;
    }

    private final void assertQueue(String name) throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put("x-max-length", 64);
        boolean passive = false;
        boolean durable = false;
        boolean autoDelete = true;
        this.queue = getChannel().queueDeclare(name, passive, durable, autoDelete, options).getQueue();
    }

    public Router(com.rabbitmq.client.Channel channel) throws IOException {
        super(channel);
        assertQueue("");
        boolean autoAck = true;
        channel.basicConsume(queue, autoAck, this);
    }

    ArrayBlockingQueue<Message> addRoute(String exchange, String topic) throws IOException {
        String key = exchange + "/" + topic;
        ArrayBlockingQueue<Message> blockingQueue = new ArrayBlockingQueue(32);
        routes.put(key, blockingQueue);
        return blockingQueue;
    }

    void removeRoute(String exchange, String topic) throws IOException {
        String key = exchange + "/" + topic;
        routes.remove(key);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        String route = envelope.getExchange() + "/" + envelope.getRoutingKey();

        ArrayBlockingQueue<Message> blockingQueue = routes.get(route);

        if (blockingQueue != null) {
            //log.info("New message @" + route);
            Message message = new Message(consumerTag, envelope, properties, body);
            blockingQueue.offer(message);
        } else {
            log.info("Received unexpected message @" + route);
        }
    }

}
