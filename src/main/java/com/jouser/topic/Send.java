package com.jouser.topic;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Send {

    private static final String EXCHANGE_NAME = "test_exchange_topic";

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection conn = ConnUtil.getConn();

        final Channel channel = conn.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String routingKey = "goods.add";

        String message = "hello topic";

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());

        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        conn.close();

    }
}
