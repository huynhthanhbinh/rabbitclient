package com.bht.rabbitclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bht.rabbitclient.verticle.VertXRESTVerticle;
import com.bht.rabbitclient.verticle.VertXRabbitMqVerticle;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRabbitMqClient extends Launcher {

    private static final Logger log = LogManager.getLogger(VertXRabbitMqClient.class);

    public static void main(String[] args) {
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
        log.info("Starting RabbitMQ Client v1.0 by binh.huynh1");

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(VertXRabbitMqVerticle.class.getCanonicalName());
        vertx.deployVerticle(VertXRESTVerticle.class.getCanonicalName());
    }
}