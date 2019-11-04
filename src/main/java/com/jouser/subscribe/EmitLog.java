package com.jouser.subscribe;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>发布订阅</p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection conn = ConnUtil.getConn();

        final Channel channel = conn.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        String message = "hello rabbitmq";

        channel.basicPublish(EXCHANGE_NAME,"", null, message.getBytes());

        System.out.println(" [x] Sent '" + message + "'");

    }

}
