package com.bht.rabbitclient.common;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 *
 * @author binhhuynh1
 */
public final class ConfigData {

    private static class FieldName {

        private FieldName() {
        }

        private static final String LISTENING_PORT = "listening_port";
        private static final String LISTENING_PATH = "listening_path";
        private static final String PRODUCER_QUEUE_MESSAGE_TTL = "producer_queue_message_ttl";
        private static final String CONSUMER_QUEUE_NAME_PREFIX = "consumer_queue_name_prefix";
        private static final String RABBITMQ_CONNECTION_INFO = "rabbitmq_connection_info";
        private static final String HOST = "host";
        private static final String PORT = "port";
        private static final String USER = "user";
        private static final String PASS = "pass";
    }

    private final RabbitMQOptions rabbitMQOptions;
    private final String listeningPath;
    private final int listeningPort;
    private final int producerQueueMessageTTL;
    private final String consumerQueueNamePrefix;

    public ConfigData(String configDataRaw) {
        this(new JsonObject(configDataRaw));
    }

    public ConfigData(JsonObject configDataJO) {
        listeningPath = Objects.requireNonNull(configDataJO.getString(FieldName.LISTENING_PATH));
        listeningPort = Objects.requireNonNull(configDataJO.getInteger(FieldName.LISTENING_PORT));
        producerQueueMessageTTL = Objects.requireNonNull(configDataJO.getInteger(FieldName.PRODUCER_QUEUE_MESSAGE_TTL));
        consumerQueueNamePrefix = Objects.requireNonNull(configDataJO.getString(FieldName.CONSUMER_QUEUE_NAME_PREFIX));
        JsonObject rabbitMqOptionsJO = Objects.requireNonNull(configDataJO.getJsonObject(FieldName.RABBITMQ_CONNECTION_INFO));
        rabbitMQOptions = new RabbitMQOptions()
                .setHost(Objects.requireNonNull(rabbitMqOptionsJO.getString(FieldName.HOST)))
                .setPort(Objects.requireNonNull(rabbitMqOptionsJO.getInteger(FieldName.PORT)))
                .setUser(Objects.requireNonNull(rabbitMqOptionsJO.getString(FieldName.USER)))
                .setPassword(Objects.requireNonNull(rabbitMqOptionsJO.getString(FieldName.PASS)));
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put(FieldName.LISTENING_PORT, listeningPort)
                .put(FieldName.LISTENING_PATH, listeningPath)
                .put(FieldName.PRODUCER_QUEUE_MESSAGE_TTL, producerQueueMessageTTL)
                .put(FieldName.CONSUMER_QUEUE_NAME_PREFIX, consumerQueueNamePrefix)
                .put(FieldName.RABBITMQ_CONNECTION_INFO, new JsonObject()
                        .put(FieldName.HOST, rabbitMQOptions.getHost())
                        .put(FieldName.PORT, rabbitMQOptions.getPort())
                        .put(FieldName.USER, rabbitMQOptions.getUser())
                        .put(FieldName.PASS, rabbitMQOptions.getPassword()));
    }

    @Override
    public String toString() {
        return toJson().encodePrettily();
    }

    public RabbitMQOptions getRabbitMQOptions() {
        return rabbitMQOptions;
    }

    public String getListeningPath() {
        return listeningPath;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public int getProducerQueueMessageTTL() {
        return producerQueueMessageTTL;
    }

    public String getConsumerQueueNamePrefix() {
        return consumerQueueNamePrefix;
    }
}