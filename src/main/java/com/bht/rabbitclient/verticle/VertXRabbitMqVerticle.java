package com.bht.rabbitclient.verticle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bht.rabbitclient.handler.VertXRabbitMqHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRabbitMqVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(VertXRabbitMqVerticle.class);
    public static final String CONSUMER_NAME = VertXRabbitMqVerticle.class.getSimpleName();
    private static final Integer PERIOD_IN_MILLIS = 30000;

    public static final RabbitMQOptions RABBIT_MQ_OPTIONS = new RabbitMQOptions()
            .setHost("localhost")
            .setPort(5672)
            .setUser("test")
            .setPassword("test");

    @Override
    public void start(Promise<Void> startFuture) {
        log.info("Starting VertXRabbitMqVerticle ...");
        RabbitMQClient rabbitMQClient = RabbitMQClient.create(vertx, RABBIT_MQ_OPTIONS);
        VertXRabbitMqHandler rabbitMQService = new VertXRabbitMqHandler(rabbitMQClient);

        rabbitMQClient.start(rabbitMQService::handleCreateConnection);
        vertx.eventBus().consumer(CONSUMER_NAME, rabbitMQService::handlePublishMessage);
        vertx.setPeriodic(PERIOD_IN_MILLIS, rabbitMQService::runScheduledTask);
        startFuture.complete();
    }

    @Override
    public void stop() {
        log.info("Shutting down VertXRabbitMqVerticle ...");
    }
}