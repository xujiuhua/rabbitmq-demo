package com.jouser.only.drop;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 成功消费需要两个：1.必须手动确认 2.必须回执确认
 * </p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Recv3 {
    private final static String TASK_QUEUE_NAME = "test_drop";

    public static void main(String[] argv) throws Exception {

        Connection connection = ConnUtil.getConn();

        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received '" + message + "'");

            try {
                System.out.println(" [x] Handle start '" + message + "'");
                // 业务代码，比如插入数据库等等
                System.out.println(" [x] Handle end '" + message + "'");
                System.out.println(" [x] Done");
                // 2. 回执确认
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // 1. 手动确认
        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });

    }
}
