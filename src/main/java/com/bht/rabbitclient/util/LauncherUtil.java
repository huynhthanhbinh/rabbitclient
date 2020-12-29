package com.bht.rabbitclient.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;

import com.bht.rabbitclient.common.ConfigData;
import com.bht.rabbitclient.verticle.VertXRESTVerticle;
import com.bht.rabbitclient.verticle.VertXRabbitMqVerticle;

import io.vertx.core.Vertx;

/**
 *
 * @author binhhuynh1
 */
public final class LauncherUtil {

    private static final Logger log = LogManager.getLogger(LauncherUtil.class);
    private static ConfigData configData;

    private LauncherUtil() {
    }

    public static void launch(String cfgFileName) {
        String cfgFilePath = Paths.get("").toAbsolutePath().toString() + '/' + cfgFileName;
        loadConfigFromFile(cfgFilePath);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(VertXRabbitMqVerticle.class.getCanonicalName());
        vertx.deployVerticle(VertXRESTVerticle.class.getCanonicalName());
    }

    private static void loadConfigFromFile(String cfgFilePath) {
        try (FileReader fileReader = new FileReader(cfgFilePath)) {
            String configDataRaw = IOUtils.toString(Objects.requireNonNull(fileReader));
            configData = new ConfigData(configDataRaw);
            log.info("Successfully read config data from {}\n{}", cfgFilePath, configData);

        } catch (IOException | NullPointerException exception) {
            InputStream stream = LauncherUtil.class.getClassLoader().getResourceAsStream("default.cfg.json");
            configData = new ConfigData(inputStreamToString(stream));
            log.error("Cannot read or parse config data from {}\n", cfgFilePath, exception);
            log.info("Using default config data:\n{}", configData);
        }
    }

    public static ConfigData getConfigData() {
        return configData;
    }

    private static String inputStreamToString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}