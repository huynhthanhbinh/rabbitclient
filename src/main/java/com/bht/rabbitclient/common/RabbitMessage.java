package com.bht.rabbitclient.common;

import java.util.Objects;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author binhhuynh1
 */
public final class RabbitMessage {

    private final String correlationId;
    private final String replyQueueName;
    private final JsonObject body;

    public JsonObject toJson() {
        return new JsonObject()
                .put("body", this.body)
                .put("properties", buildPropertiesJson());
    }

    private JsonObject buildPropertiesJson() {
        return new JsonObject()
                .put("contentType", "application/json")
                .put("contentEncoding", "UTF-8")
                .put("replyTo", Objects.requireNonNull(this.replyQueueName))
                .put("correlationId", Objects.requireNonNull(this.correlationId));
    }

    private RabbitMessage(String correlationId, String replyQueueName, JsonObject body) {
        this.correlationId = correlationId;
        this.replyQueueName = replyQueueName;
        this.body = body;
    }

    public static RabbitMessage.RabbitMessageBuilder builder() {
        return new RabbitMessage.RabbitMessageBuilder();
    }

    public static class RabbitMessageBuilder {
        private String correlationId;
        private String replyQueueName;
        private JsonObject body;

        private RabbitMessageBuilder() {
        }

        public RabbitMessage.RabbitMessageBuilder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public RabbitMessage.RabbitMessageBuilder replyQueueName(String replyQueueName) {
            this.replyQueueName = replyQueueName;
            return this;
        }

        public RabbitMessage.RabbitMessageBuilder body(JsonObject body) {
            this.body = body;
            return this;
        }

        public RabbitMessage build() {
            return new RabbitMessage(this.correlationId, this.replyQueueName, this.body);
        }
    }
}