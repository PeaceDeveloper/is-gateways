package com.labviros.is;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

/**
 * Created by picoreti on 08/04/17.
 */
public class Message {
    private String consumerTag;
    private Envelope envelope;
    private AMQP.BasicProperties properties;
    private AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
    private byte[] body;

    public void pack() throws Exception {
        //throw new Exception("Not Implemented");
    }

    public void unpack() throws Exception {
        //throw new Exception("Not Implemented");
    }

    public Message() { }

    public Message(Message copy) {
        this(copy.getConsumerTag(), copy.getEnvelope(), copy.getProperties(), copy.getBody());
    }

    public Message(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        this.consumerTag = consumerTag;
        this.envelope = envelope;
        this.properties = properties;
        this.body = body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public AMQP.BasicProperties getProperties() {
        return properties;
    }

    public AMQP.BasicProperties.Builder getPropertiesBuilder() {
        return builder;
    }

    public byte[] getBody() {
        return body;
    }
}
