package com.bht.rabbitclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRESTHandler implements Handler<RoutingContext> {

    private static final Logger log = LogManager.getLogger(VertXRESTHandler.class);

    @Override
    public void handle(RoutingContext routingContext) {
        
    }
}