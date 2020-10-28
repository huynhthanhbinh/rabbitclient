package com.bht.rabbitclient.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bht.rabbitclient.verticle.VertXRabbitMqVerticle;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRESTHandler implements Handler<RoutingContext> {

    private static final Logger log = LogManager.getLogger(VertXRESTHandler.class);
    private final Vertx vertx;
    private final DeliveryOptions deliveryOptions;

    private static final String EMPTY_STRING = "";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    private static final String QUEUE = "queue";
    private static final String DATA = "data";

    private static final int HTTP_BAD_REQUEST_CODE = 400;
    private static final int HTTP_SUCCESS_RESPONSE_CODE = 200;
    private static final long EVENT_BUS_TIME_OUT = 30000L;

    public VertXRESTHandler(Vertx vertx) {
        this.vertx = vertx;
        deliveryOptions = new DeliveryOptions().setSendTimeout(EVENT_BUS_TIME_OUT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8);
        response.setChunked(true);

        HttpServerRequest request = routingContext.request();
        request.bodyHandler(buffer -> {
            JsonObject jsonObject = buffer.toJsonObject();
            log.info("New request from: {}\n ", request.remoteAddress().host());
            log.info("Request data: {}\n", new String(buffer.getBytes()));

            String producerEndpoint = jsonObject.getString(QUEUE, EMPTY_STRING);
            if (producerEndpoint.equalsIgnoreCase(EMPTY_STRING)) {
                response.setStatusCode(HTTP_BAD_REQUEST_CODE);
                response.setStatusMessage("Queue name is empty!");
                response.end();
                return;
            }

            JsonObject data = jsonObject.getJsonObject(DATA, new JsonObject());
            vertx.eventBus().send(VertXRabbitMqVerticle.CONSUMER_NAME, data, deliveryOptions, output -> {
                if (output.failed()) {

                } else {

                }
            });
        });
    }
}