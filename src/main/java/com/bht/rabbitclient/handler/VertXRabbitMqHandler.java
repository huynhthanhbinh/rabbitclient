package com.bht.rabbitclient.handler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bht.rabbitclient.common.RabbitMessage;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQMessage;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRabbitMqHandler {

    private static final Logger log = LogManager.getLogger(VertXRabbitMqHandler.class);
    public static final String PRODUCER_QUEUE_KEY = "QUEUE_NAME";
    public static final String CONSUMER_QUEUE_NAME = "replyQueue_" + UUID.randomUUID().toString().split("-")[4];

    private static final Map<String, Handler<JsonObject>> MAP_JOBS = new ConcurrentHashMap<>();
    private static final Map<String, Long> MAP_EXPIRATIONS = new ConcurrentHashMap<>();
    private static final Long EXP_IN_MILLIS = 60000L;
    private final RabbitMQClient rabbitMQClient;

    public VertXRabbitMqHandler(RabbitMQClient rabbitMQClient) {
        this.rabbitMQClient = rabbitMQClient;
    }

    public void handlePublishMessage(Message<JsonObject> event) {
        RabbitMessage message = RabbitMessage.builder()
                .body(event.body())
                .correlationId(UUID.randomUUID().toString())
                .replyQueueName(CONSUMER_QUEUE_NAME)
                .build();

        String producerQueueName = event.headers().get(PRODUCER_QUEUE_KEY);
        publishMessageToQueue(message.toJson(), producerQueueName, EXP_IN_MILLIS, reply -> handleResponse(reply, event));
    }

    @SuppressWarnings("SameParameterValue")
    private void publishMessageToQueue(JsonObject message,
                                       String producerQueueName,
                                       long expInMillis,
                                       Handler<JsonObject> callback) {

        log.info("publish message to queue \"{}\": {}", producerQueueName, message);
        String correlationId = message.getJsonObject("properties").getString("correlationId");
        rabbitMQClient.basicPublish("", producerQueueName, message,
                result -> handlePublishMessageToQueue(result, producerQueueName, correlationId, expInMillis, callback));
    }

    private void handlePublishMessageToQueue(AsyncResult<Void> result,
                                             String producerQueueName,
                                             String correlationId,
                                             long expInMillis,
                                             Handler<JsonObject> callback) {
        if (result.failed()) {
            log.error("Publish message <{}> to queue \"{}\" failed", correlationId, producerQueueName, result.cause());
            result.cause().printStackTrace();
        } else {
            log.info("Publish message <{}>  to queue \"{}\" succeeded", correlationId, producerQueueName);
            MAP_EXPIRATIONS.put(correlationId, System.currentTimeMillis() + expInMillis);
            MAP_JOBS.put(correlationId, callback);
        }
    }

    private void handleResponse(JsonObject response, Message<JsonObject> event) {
        log.info("Receive new response:\n{}\n", response);
        event.reply(response);
    }

    public void handleCreateConnection(AsyncResult<Void> res, RabbitMQOptions rabbitMQOptions) {
        if (res.succeeded()) {
            log.info("connect to RabbitMQ succeeded");
            log.info("connection info:\n{\n\thost: {},\n\tport: {},\n\tusername: {},\n\tpassword: {}\n}",
                    rabbitMQOptions.getHost(),
                    rabbitMQOptions.getPort(),
                    rabbitMQOptions.getUser(),
                    rabbitMQOptions.getPassword());

            rabbitMQClient.queueDeclare(CONSUMER_QUEUE_NAME, true, false, true, declareQueueResult -> {
                if (declareQueueResult.succeeded()) {
                    log.info("auto-delete queue declare succeeded: {}", CONSUMER_QUEUE_NAME);
                    rabbitMQClient.basicConsumer(CONSUMER_QUEUE_NAME, this::handleDeclareConsumer);
                } else {
                    log.error("auto-delete queue declare failed: {}, {}", CONSUMER_QUEUE_NAME, declareQueueResult.cause().getMessage());
                }
            });
        } else {
            log.error("connect to RabbitMQ failed", res.cause());
            log.error("connection info:\n{\n\thost: {},\n\tport: {},\n\tusername: {},\n\tpassword: {}\n}",
                    rabbitMQOptions.getHost(),
                    rabbitMQOptions.getPort(),
                    rabbitMQOptions.getUser(),
                    rabbitMQOptions.getPassword());
            System.exit(-1);
        }
    }

    private void handleDeclareConsumer(AsyncResult<RabbitMQConsumer> declareConsumerResult) {
        if (declareConsumerResult.failed()) {
            log.error("failed create consumer of queue: {}", CONSUMER_QUEUE_NAME);
            declareConsumerResult.cause().printStackTrace();
        } else {
            log.info("succeeded create consumer of queue: {}", CONSUMER_QUEUE_NAME);
            declareConsumerResult.result().handler(this::consumeMessageFromQueue);
        }
    }

    private void consumeMessageFromQueue(RabbitMQMessage message) {
        String correlationId = message.properties().correlationId();

        /* if job had been created before --> processing, otherwise, ignore processing message */
        if (MAP_JOBS.containsKey(correlationId)) {
            MAP_JOBS.remove(correlationId)
                    .handle(new JsonObject(message.body()));
        }
    }

    @SuppressWarnings("unused")
    public void runScheduledTask(Long value) {
        long current = System.currentTimeMillis();
        List<String> expiredList = MAP_EXPIRATIONS.entrySet().stream()
                .filter(x -> x.getValue() <= current)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        MAP_JOBS.keySet().removeAll(expiredList);
        MAP_EXPIRATIONS.keySet().removeAll(expiredList);

        expiredList.forEach(this::notifyTimedOutMessage);
    }

    private void notifyTimedOutMessage(String messageCorrelationId) {
        log.warn("Timed out waiting for response of message: <{}>", messageCorrelationId);
    }
}