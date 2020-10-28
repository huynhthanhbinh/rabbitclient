package com.bht.rabbitclient.verticle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRabbitMqVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(VertXRabbitMqVerticle.class);
    public static final String CONSUMER_NAME = VertXRabbitMqVerticle.class.getSimpleName();

    @Override
    public void start(Promise<Void> startFuture) {
        log.info("Starting VertXRabbitMqVerticle ...");
        connectToRabbitMqServer(startFuture);
    }

    private void connectToRabbitMqServer(Promise<Void> startFuture) {
        startFuture.complete();
    }
}