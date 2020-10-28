package com.bht.rabbitclient;

import io.vertx.core.Vertx;

/**
 *
 * @author bht
 */
public class VertXRabbitMqClient {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(VertXRESTVerticle.class.getCanonicalName());
    }
}