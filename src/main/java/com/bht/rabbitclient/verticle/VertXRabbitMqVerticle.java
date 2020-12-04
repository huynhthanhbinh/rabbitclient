package com.bht.rabbitclient.verticle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bht.rabbitclient.handler.VertXRabbitMqHandler;
import com.bht.rabbitclient.util.LauncherUtil;

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

    @Override
    public void start(Promise<Void> startFuture) {
        log.info("Starting VertXRabbitMqVerticle ...");
        RabbitMQOptions rabbitMQOptions = LauncherUtil.getConfigData().getRabbitMQOptions();
        RabbitMQClient rabbitMQClient = RabbitMQClient.create(vertx, rabbitMQOptions);
        VertXRabbitMqHandler rabbitMQService = new VertXRabbitMqHandler(rabbitMQClient);
        rabbitMQClient.start(res -> rabbitMQService.handleCreateConnection(res, rabbitMQOptions));
        vertx.eventBus().consumer(CONSUMER_NAME, rabbitMQService::handlePublishMessage);
        vertx.setPeriodic(PERIOD_IN_MILLIS, rabbitMQService::runScheduledTask);
        startFuture.complete();
    }

    @Override
    public void stop() {
        log.info("Shutting down VertXRabbitMqVerticle ...");
    }
}