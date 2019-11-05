package com.jouser.persistent;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.util.concurrent.TimeoutException;

/**
 * <p>
 *     设置了队列和消息的持久化之后，当broker服务重启的之后，消息依旧存在。
 *     单只设置队列持久化，重启之后消息会丢失；
 *     单只设置消息的持久化，重启之后队列消失，既而消息也丢失。
 *     单设置消息持久化而不设置队列的持久化显得毫无意义
 * </p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Send {
    private final static String QUEUE_NAME = "persistent";

    public static void main(String[] argv)
            throws java.io.IOException, TimeoutException {

        Connection connection = ConnUtil.getConn();

        Channel channel = connection.createChannel();

        // 队列持久化
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        String message = "Hello World!";

        // 消息持久化
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
