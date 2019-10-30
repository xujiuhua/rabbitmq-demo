package com.jouser.tx;

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

   private final static String QUEUE_NAME = "test_queue_tx";

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = ConnUtil.getConn();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "Hello TX!";

        try {
            channel.txSelect();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

            int a = 1 / 0;

            channel.txCommit();
            System.out.println(" [x] Sent '" + message + "'");
        } catch (Exception e) {
            channel.txRollback();
            System.out.println("tx rollback");
        } finally {
            channel.close();
            connection.close();
        }

    }
}
