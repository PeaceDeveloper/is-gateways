package com.labviros.is;

import com.rabbitmq.client.AMQP;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ServiceClient {

    private final static Logger log = LogManager.getLogger(ServiceClient.class.getName());

    private final ArrayBlockingQueue<Message> replies;
    private final String queue;
    private final com.rabbitmq.client.Channel channel;

    public ServiceClient(com.rabbitmq.client.Channel channel, ArrayBlockingQueue<Message> replies, String queue) {
        this.channel = channel;
        this.replies = replies;
        this.queue = queue;
    }

    public final String request(String service, Message request) throws Exception {
        if (request.getPropertiesBuilder().build().getCorrelationId() == null) {
            request.getPropertiesBuilder().correlationId(UUID.randomUUID().toString());
        }
        request.getPropertiesBuilder().replyTo(queue);

        AMQP.BasicProperties properties = request.getPropertiesBuilder().build();
        request.pack();

        channel.basicPublish("services", service, properties, request.getBody());
        return properties.getCorrelationId();
    }

    public final Message receive(long timeout, TimeUnit unit) throws InterruptedException {
        return replies.poll(timeout, unit);
    }

    public final Message receiveDiscardOthers(String id, long timeout, TimeUnit unit) throws InterruptedException {
        long nanoTimeout = unit.toNanos(timeout);

        while (nanoTimeout > 0) {
            long before = System.nanoTime();
            Message message = receive(nanoTimeout, TimeUnit.NANOSECONDS);
            if (message == null || message.getProperties().getCorrelationId().equals(id)) {
                return message;
            }
            long after = System.nanoTime();
            nanoTimeout -= after - before;
        }

        return null;
    }

}
