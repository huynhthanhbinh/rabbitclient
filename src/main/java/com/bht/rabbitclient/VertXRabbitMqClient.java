package com.bht.rabbitclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.vertx.core.Vertx;

/**
 *
 * @author bht
 */
public class VertXRabbitMqClient {

    private static final Logger log = LogManager.getLogger(VertXRabbitMqClient.class);

    public static void main(String[] args) {
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
        log.info("Starting RabbitMQ Client v1.0 by binh.huynh1");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(VertXRESTVerticle.class.getCanonicalName());
    }
}