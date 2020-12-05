package com.bht.rabbitclient.verticle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bht.rabbitclient.handler.VertXRESTHandler;
import com.bht.rabbitclient.util.LauncherUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

/**
 *
 * @author binhhuynh1
 */
public final class VertXRESTVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(VertXRESTVerticle.class);

    @Override
    public void start(Promise<Void> startFuture) {
        log.info("Starting VertXRESTVerticle ...");
        startHttpServer(startFuture);
    }

    @Override
    public void stop() {
        log.info("Shutting down VertXRESTVerticle ...");
    }

    private void startHttpServer(Promise<Void> startFuture) {
        Router router = Router.router(vertx);
        router.post(LauncherUtil.getConfigData().getListeningPath()).handler(new VertXRESTHandler(getVertx()));

        HttpServerOptions options = new HttpServerOptions();
        options.setPort(LauncherUtil.getConfigData().getListeningPort());

        HttpServer httpServer = vertx
                .createHttpServer(options)
                .requestHandler(router);

        httpServer.listen(asyncResult -> {
            if (asyncResult.failed()) {
                log.error("HTTP server started fail!", asyncResult.cause());
                startFuture.fail(asyncResult.cause());
                System.exit(-1);
            } else {
                log.info("HTTP server started, listening on port: {}, SSL enabled: {}", options.getPort(), options.isSsl());
                startFuture.complete();
            }
        });
    }
}