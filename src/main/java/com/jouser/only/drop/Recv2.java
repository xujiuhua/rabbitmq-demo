package com.jouser.only.drop;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 消费失败，消息不会丢失, 但是只要程序一启动开始接收该队列消息的时候,又会收到
 * </p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Recv2 {
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
                int a = 1/0;
                System.out.println(" [x] Handle end '" + message + "'");
                System.out.println(" [x] Done");

                // 2. 确认消费完成，如果发生异常，消息状态为：Unacked; 只要程序一启动开始接收该队列消息的时候 又会收到
                // 此处不确认，则状态为unacked
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
