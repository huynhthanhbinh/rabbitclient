package com.bht.rabbitclient.util;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;

import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 *
 * @author binhhuynh1
 */
public final class LauncherUtil {

    private static final Logger log = LogManager.getLogger(LauncherUtil.class);
    private static final String CFG_FILE_NAME = "rabbitclient.cfg.json";

    private static final String LISTENING_PORT = "listening_port";
    private static final String LISTENING_PATH = "listening_path";
    private static final String RABBITMQ_CONNECTION_INFO = "rabbitmq_connection_info";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String USER = "user";
    private static final String PASS = "pass";

    private static final JsonObject DEFAULT_CONFIG = new JsonObject()
            .put(LISTENING_PORT, 1234)
            .put(LISTENING_PATH, "/rabbitClient")
            .put(RABBITMQ_CONNECTION_INFO, new JsonObject()
                    .put(HOST, "localhost")
                    .put(PORT, 5672)
                    .put(USER, "test")
                    .put(PASS, "test"));

    private static JsonObject launcherConfigData;
    private static RabbitMQOptions rabbitMQOptions;

    private LauncherUtil() {
    }

    public static void loadConfigFromFile() {
        String cfgPath = Paths.get("").toAbsolutePath().toString() + '/' + CFG_FILE_NAME;

        try (FileReader fileReader = new FileReader(cfgPath)) {
            String configDataRaw = IOUtils.toString(Objects.requireNonNull(fileReader));
            launcherConfigData = new JsonObject(configDataRaw);
            String configDataFormat = launcherConfigData.encodePrettily();
            log.info("Successfully read config data from {}\n{}", cfgPath, configDataFormat);

        } catch (IOException | NullPointerException exception) {
            launcherConfigData = DEFAULT_CONFIG;
            String configDataFormat = launcherConfigData.encodePrettily();
            log.error("Cannot read config data from {}\n", cfgPath, exception);
            log.info("Using default config data:\n{}", configDataFormat);

        } finally {
            JsonObject rabbitMqOptionsJO = launcherConfigData.getJsonObject(RABBITMQ_CONNECTION_INFO);
            RabbitMQOptions rabbitMQOptions = new RabbitMQOptions()
                    .setHost(rabbitMqOptionsJO.getString(HOST))
                    .setPort(rabbitMqOptionsJO.getInteger(PORT))
                    .setUser(rabbitMqOptionsJO.getString(USER))
                    .setPassword(rabbitMqOptionsJO.getString(PASS));
        }
    }
}