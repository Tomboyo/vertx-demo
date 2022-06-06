package com.github.tomboyo.vertxdemo;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;

public class MainVerticle extends AbstractVerticle {

  private int port = -1;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigRetriever.create(vertx)
    .getConfig()
    .compose(this::launchHttpServer)
    .onSuccess(httpServer -> {
      startPromise.complete();
      port = httpServer.actualPort();
      System.out.println("HTTP server started on port " + port);
    })
    .onFailure(cause -> {
      startPromise.fail(cause);
    });
  }

  private Future<HttpServer> launchHttpServer(JsonObject config) {
    var portConfig = config.getInteger("main.verticle.http.port", 8080);
    return vertx.createHttpServer(httpServerOptions(config))
              .requestHandler(this::defaultHandler)
              .listen(portConfig);
  }

  private void defaultHandler(HttpServerRequest req) {
      req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!");
  }

  public int port() {
    if (port == -1) {
      throw new IllegalStateException("Port cannot be retrieved because the verticle has not started");
    }
    return port;
  }

  private HttpServerOptions httpServerOptions(JsonObject config) {
    return new HttpServerOptions()
        .setUseAlpn(true)
        .setSsl(true)
        .setKeyStoreOptions(new JksOptions()
            .setPath(config.getString("main.verticle.p12.path", "/etc/vertx-demo/certs/server.p12"))
            .setPassword(config.getString("main.verticle.p12.password", "password")));
  }
}
