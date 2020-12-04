package com.bht.rabbitclient.common;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 *
 * @author binhhuynh1
 */
@SuppressWarnings("java:S1075")
public final class ConfigData {

    private static final String LISTENING_PORT = "listening_port";
    private static final String LISTENING_PATH = "listening_path";
    private static final String RABBITMQ_CONNECTION_INFO = "rabbitmq_connection_info";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String USER = "user";
    private static final String PASS = "pass";
    private static final ConfigData DEFAULT_CONFIG = new ConfigData();

    private final RabbitMQOptions rabbitMQOptions;
    private final String listeningPath;
    private final int listeningPort;

    public static ConfigData getDefaultConfig() {
        return DEFAULT_CONFIG;
    }

    private ConfigData() {
        listeningPort = 1234;
        listeningPath = "/rabbitClient";
        rabbitMQOptions = new RabbitMQOptions()
                .setHost("localhost")
                .setPort(5672)
                .setUser("test")
                .setPassword("test");
    }
    
    public ConfigData(String configDataRaw) {
        this(new JsonObject(configDataRaw));
    }

    public ConfigData(JsonObject configDataJO) {
        listeningPath = Objects.requireNonNull(configDataJO.getString(LISTENING_PATH));
        listeningPort = Objects.requireNonNull(configDataJO.getInteger(LISTENING_PORT));
        JsonObject rabbitMqOptionsJO = Objects.requireNonNull(configDataJO.getJsonObject(RABBITMQ_CONNECTION_INFO));
        rabbitMQOptions = new RabbitMQOptions()
                .setHost(Objects.requireNonNull(rabbitMqOptionsJO.getString(HOST)))
                .setPort(Objects.requireNonNull(rabbitMqOptionsJO.getInteger(PORT)))
                .setUser(Objects.requireNonNull(rabbitMqOptionsJO.getString(USER)))
                .setPassword(Objects.requireNonNull(rabbitMqOptionsJO.getString(PASS)));
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put(LISTENING_PORT, listeningPort)
                .put(LISTENING_PATH, listeningPath)
                .put(RABBITMQ_CONNECTION_INFO, new JsonObject()
                        .put(HOST, rabbitMQOptions.getHost())
                        .put(PORT, rabbitMQOptions.getPort())
                        .put(USER, rabbitMQOptions.getUser())
                        .put(PASS, rabbitMQOptions.getPassword()));
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
}