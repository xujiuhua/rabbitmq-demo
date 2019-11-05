package com.jouser.workfair;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Recv1 {

    private final static String TASK_QUEUE_NAME = "task_queue_fair";

    public static void main(String[] argv) throws Exception {

        Connection connection = ConnUtil.getConn();

        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 保证一次只分发一个
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received '" + message + "'");
            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        // 自动应答改为false
        final boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });

    }

    private static void doWork(String task) {
        try {
            System.out.println("我已消费到: " + task);
            Thread.sleep(10_000);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }

}
