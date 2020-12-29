package com.bht.rabbitclient.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bht.rabbitclient.util.LauncherUtil;
import com.bht.rabbitclient.verticle.VertXRabbitMqVerticle;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
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
    private static final String QUEUE = "queue";
    private static final String DATA = "data";
    private static final long EVENT_BUS_TIME_OUT = LauncherUtil.getConfigData().getProducerQueueMessageTTL();
    private final Vertx vertx;

    public VertXRESTHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void handle(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=utf-8");
        response.setChunked(true);

        HttpServerRequest request = routingContext.request();
        request.bodyHandler(buffer -> {
            try {
                JsonObject requestJO = buffer.toJsonObject();
                log.info("Request data:\n{}\n", new String(buffer.getBytes()));

                String producerEndpoint = requestJO.getString(QUEUE, StringUtil.EMPTY_STRING);
                if (producerEndpoint.equals(StringUtil.EMPTY_STRING)) {
                    response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
                    response.setStatusMessage("Queue name is missing or empty!");
                    response.end();
                    return;
                }

                JsonObject data = requestJO.getJsonObject(DATA, new JsonObject());
                DeliveryOptions deliveryOptions = new DeliveryOptions()
                        .setSendTimeout(EVENT_BUS_TIME_OUT)
                        .addHeader(VertXRabbitMqHandler.PRODUCER_QUEUE_KEY, producerEndpoint);

                vertx.eventBus().send(VertXRabbitMqVerticle.CONSUMER_NAME, data, deliveryOptions, output -> {
                    if (output.failed()) {
                        response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                        response.setStatusMessage(output.cause().getMessage());
                        response.end();
                    } else {
                        response.setStatusCode(HttpResponseStatus.OK.code());
                        response.end(((JsonObject) output.result().body()).encode());
                    }
                });
            } catch (Exception exception) {
                response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
                response.setStatusMessage(exception.getMessage());
                response.end();
                log.error("{}: {}", exception.getClass().getSimpleName(), exception.getMessage());
            }
        });
    }
}