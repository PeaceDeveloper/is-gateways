package com.labviros.is;

import com.google.common.base.CaseFormat;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by picoreti on 09/04/17.
 */
public class ServiceProvider<T> extends DefaultConsumer {
    private final static Logger log = LogManager.getLogger(ServiceProvider.class.getName());

    private final T impl;
    private final HashMap<String, Service> services;
    private final String entity;

    private final void assertQueue(String name) throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put("x-max-length", 64);
        options.put("x-expires", 30000);
        boolean passive = false;
        boolean durable = false;
        boolean autoDelete = false;
        getChannel().queueDeclare(name, passive, durable, autoDelete, options);
    }

    public ServiceProvider(Channel channel, T impl) throws IOException {
        super(channel);
        this.impl = impl;
        this.entity = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, impl.getClass().getSimpleName());
        this.services = buildServiceMap(impl.getClass(), entity);
        assertQueue(entity);

        for (HashMap.Entry<String, Service> entry : services.entrySet()) {
            getChannel().queueBind(entity, "services", entry.getKey());
        }

        boolean autoAck = false;
        getChannel().basicConsume(entity, autoAck, this);
    }

    public final void stop() throws IOException {
        getChannel().queueDelete(entity);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        String route = envelope.getRoutingKey();
        Service service = services.get(route);

        if (service != null) {
            log.info("New service request @" + route);

            try {
                Message requestMessage = new Message(consumerTag, envelope, properties, body);
                Constructor constructor = service.getRequestType().getConstructor(Message.class);
                Object request = constructor.newInstance(requestMessage);

                Object reply = service.getImpl().invoke(impl, request);

                Message replyMessage = (Message) reply;
                replyMessage.getPropertiesBuilder()
                        .contentType("application/msgpack")
                        .correlationId(properties.getCorrelationId())
                        .build();

                replyMessage.pack();

                getChannel().basicPublish("services", properties.getReplyTo(), replyMessage.getPropertiesBuilder().build(), replyMessage.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            log.info("Unexpected service request @" + route);
        }

        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }


    private final Method findMethod(Class c, String name) throws NoSuchMethodException {
        for (Method m : c.getMethods()) {
            if (m.getName() == name) {
                return m;
            }
        }
        throw new NoSuchMethodException();
    }

    private final HashMap<String, Service> buildServiceMap(Class c, String entity) {
        HashMap<String, Service> services = new HashMap<>();

        java.lang.reflect.Method[] methods = c.getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            String name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());

            Class<?>[] parameterTypes= method.getParameterTypes();
            Class<?> returnType = method.getReturnType();

            if (parameterTypes != null && returnType != null && parameterTypes.length == 1 &&
                    Message.class.isAssignableFrom(parameterTypes[0]) && Message.class.isAssignableFrom(returnType)) {
                String routingKey = entity + "." + name;
                services.put(routingKey, new Service(method, parameterTypes[0], returnType));
                log.info("Exposing service @method=" + method.getName());
            }
        }

        return services;
    }

}
