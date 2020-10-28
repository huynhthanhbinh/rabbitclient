package com.bht.rabbitclient.verticle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRabbitMqVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(VertXRabbitMqVerticle.class);
    public static final String CONSUMER_NAME = VertXRabbitMqVerticle.class.getSimpleName();
    public static final String QUEUE_NAME_KEY = "QUEUE_NAME";

    @Override
    public void start(Promise<Void> startFuture) {
        log.info("Starting VertXRabbitMqVerticle ...");
        connectToRabbitMqServer(startFuture);
    }

    private void connectToRabbitMqServer(Promise<Void> startFuture) {
        vertx.eventBus().<JsonObject>consumer(CONSUMER_NAME, output -> {

        });
        startFuture.complete();
    }
}