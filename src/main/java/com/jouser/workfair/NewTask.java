package com.jouser.workfair;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.concurrent.TimeoutException;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class NewTask {

    private final static String QUEUE_NAME = "task_queue_fair";

    public static void main(String[] argv)
            throws java.io.IOException, TimeoutException {

        Connection connection = ConnUtil.getConn();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        for (int i = 0; i < 50; i++) {
            String message = "work " + i;
            System.out.println(" [x] Sent '" + message + "'");
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        }

        channel.close();
        connection.close();
    }

}
