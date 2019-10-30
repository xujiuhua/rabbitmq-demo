package com.jouser.confirm;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>发送方确认模式</p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class Send {

    private final static String QUEUE_NAME = "test_queue_confirm";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Connection connection = ConnUtil.getConn();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 开启confirm模式，注意已经存在的队列设置为了txSelect, 则不允许再修改
        channel.confirmSelect();

        String message = "Hello Confirm!";

        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

        if (channel.waitForConfirms()) {
            System.out.println("send success");
        } else {
            System.out.println("send failed");
        }

        channel.close();
        connection.close();
    }

}
