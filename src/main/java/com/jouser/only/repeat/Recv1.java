package com.jouser.only.repeat;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 消费成功，但是不回执，下次只要程序一启动开始接收该队列消息的时候, 又会收到
 * 导致重复消费
 * </p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Recv1 {
    private final static String TASK_QUEUE_NAME = "test_repeat";

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
//                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
//                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });

    }
}
