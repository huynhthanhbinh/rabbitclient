package com.bht.rabbitclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bht.rabbitclient.util.LauncherUtil;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.vertx.core.Launcher;
import io.vertx.core.logging.Log4j2LogDelegateFactory;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRabbitMqClient extends Launcher {

    private static final Logger log = LogManager.getLogger(VertXRabbitMqClient.class);
    private static final String CFG_FILE_NAME = "rabbitclient.cfg.json";

    public static void main(String[] args) {
        log.info("Starting RabbitMQ Client v1.0 by binh.huynh1");
        System.setProperty("vertx.logger-delegate-factory-class-name", Log4j2LogDelegateFactory.class.getCanonicalName());
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
        LauncherUtil.launch(CFG_FILE_NAME);
    }
}