package com.jouser.confirm;

import com.jouser.util.ConnUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

/**
 * <p>
 *     https://github.com/rabbitmq/rabbitmq-tutorials/blob/master/java/PublisherConfirms.java
 * </p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class PublisherConfirms {

    static final int MESSAGE_COUNT = 50_000;

    public static void main(String[] args) throws Exception {
        publishMessagesIndividually();
        publishMessagesInBatch();
        handlePublishConfirmsAsynchronously();

    }

    static void publishMessagesIndividually() throws Exception {
        Connection conn = ConnUtil.getConn();
        Channel channel = conn.createChannel();
        String queue = UUID.randomUUID().toString() + "_confirm";
        channel.queueDeclare(queue, false, false, false, null);

        // Enabling Publisher Confirms on a Channel
        channel.confirmSelect();

        long start = System.nanoTime();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String body = String.valueOf(i);
            channel.basicPublish("", queue, null, body.getBytes());
            channel.waitForConfirmsOrDie(5_000);
        }
        long end = System.nanoTime();
        System.out.format("Published %,d messages individually in %,d ms%n", MESSAGE_COUNT, Duration.ofNanos(end - start).toMillis());
    }

    static void publishMessagesInBatch() throws Exception {
        Connection conn = ConnUtil.getConn();
        Channel channel = conn.createChannel();
        String queue = UUID.randomUUID().toString() + "_batch_confirm";
        channel.queueDeclare(queue, false, false, false, null);

        // Enabling Publisher Confirms on a Channel
        channel.confirmSelect();

        int batchSize = 100;
        int outstandingMessageCount = 0;

        long start = System.nanoTime();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String body = String.valueOf(i);
            channel.basicPublish("", queue, null, body.getBytes());
            outstandingMessageCount++;

            if (outstandingMessageCount == batchSize) {
                channel.waitForConfirmsOrDie(5_000);
                outstandingMessageCount = 0;
            }
        }
        if (outstandingMessageCount > 0) {
            channel.waitForConfirmsOrDie(5_000);
        }

        long end = System.nanoTime();
        System.out.format("Published %,d messages in batch in %,d ms%n", MESSAGE_COUNT, Duration.ofNanos(end - start).toMillis());
    }

    static void handlePublishConfirmsAsynchronously() throws Exception {
        Connection conn = ConnUtil.getConn();
        Channel channel = conn.createChannel();
        String queue = UUID.randomUUID().toString() + "_batch_confirm";
        channel.queueDeclare(queue, false, false, false, null);

        // Enabling Publisher Confirms on a Channel
        channel.confirmSelect();

        ConcurrentNavigableMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        ConfirmCallback cleanOutstandingConfirms = (sequenceNumber, multiple) -> {
            if (multiple) {
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(
                        sequenceNumber, true
                );
                confirmed.clear();
            } else {
                outstandingConfirms.remove(sequenceNumber);
            }
        };

        channel.addConfirmListener(cleanOutstandingConfirms, (sequenceNumber, multiple) -> {
            String body = outstandingConfirms.get(sequenceNumber);
            System.err.format(
                    "Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n",
                    body, sequenceNumber, multiple
            );
            cleanOutstandingConfirms.handle(sequenceNumber, multiple);
        });

        long start = System.nanoTime();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String body = String.valueOf(i);
            outstandingConfirms.put(channel.getNextPublishSeqNo(), body);
            channel.basicPublish("", queue, null, body.getBytes());
        }

        if (!waitUntil(Duration.ofSeconds(60), () -> outstandingConfirms.isEmpty())) {
            throw new IllegalStateException("All messages could not be confirmed in 60 seconds");
        }

        long end = System.nanoTime();
        System.out.format("Published %,d messages and handled confirms asynchronously in %,d ms%n", MESSAGE_COUNT, Duration.ofNanos(end - start).toMillis());

    }

    static boolean waitUntil(Duration timeout, BooleanSupplier condition) throws InterruptedException {
        int waited = 0;
        while (!condition.getAsBoolean() && waited < timeout.toMillis()) {
            Thread.sleep(100L);
            waited = +100;
        }
        return condition.getAsBoolean();
    }


}
