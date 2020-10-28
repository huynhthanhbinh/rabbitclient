package com.bht.rabbitclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 *
 * @author bht
 */
public final class VertXRESTVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(VertXRESTVerticle.class);

    @Override
    public void start(Promise<Void> startFuture) throws Exception {
        log.info("Starting VertXRESTVerticle ...");
        startFuture.complete();
        log.info("VertXRESTVerticle started ...");
    }
}