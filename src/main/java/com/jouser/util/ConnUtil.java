package com.jouser.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class ConnUtil {

    public static Connection getConn() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.211.55.17");
        factory.setPort(5672);
        factory.setVirtualHost("/vhost_hello");
        factory.setUsername("full_access");
        factory.setPassword("s3crEt");
        Connection connection = factory.newConnection();
        return connection;
    }

}
