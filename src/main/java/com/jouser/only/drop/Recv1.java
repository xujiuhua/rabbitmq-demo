package com.jouser.only.drop;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 消息丢失
 * </p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Recv1 {
    private final static String TASK_QUEUE_NAME = "test_drop";

    public static void main(String[] argv) throws Exception {

        Connection connection = ConnUtil.getConn();

        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Received '" + message + "'");

            System.out.println(" [x] Handle start '" + message + "'");

            try {
                // 还没处理完，rabbit mq异常关闭
                // 处理业务逻辑时发生异常，那么此消息未被正常消费；但消息已经从队列中删除了，导致消息丢失
                int a = 1 / 0;
                System.out.println(" [x] Handle end '" + message + "'");
                System.out.println(" [x] Done");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(TASK_QUEUE_NAME, true, deliverCallback, consumerTag -> {});

    }
}
