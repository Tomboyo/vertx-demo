package com.github.tomboyo.vertxdemo;

import static io.vertx.core.http.HttpMethod.GET;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  private int port = -1;

  @Override
  public void start(Promise<Void> startPromise) {
    configure()
        .compose(this::launchHttpServer)
        .onSuccess(
            httpServer -> {
              startPromise.complete();
              port = httpServer.actualPort();
              System.out.println("HTTP server started on port " + port);
            })
        .onFailure(startPromise::fail);
  }

  private Future<MainConfig> configure() {
    return ConfigRetriever.create(
            vertx,
            new ConfigRetrieverOptions()
                .addStore(
                    new ConfigStoreOptions()
                        .setType("file")
                        .setFormat("yaml")
                        .setConfig(new JsonObject().put("path", "conf/config.yaml"))))
        .getConfig()
        .map(json -> json.getJsonObject("main").mapTo(MainConfig.class));
  }

  private Future<HttpServer> launchHttpServer(MainConfig config) {
    var router = Router.router(vertx);
    configureRoutes(router);

    var portConfig = config.port;
    return vertx
        .createHttpServer(httpServerOptions(config))
        .requestHandler(router)
        .listen(portConfig);
  }

  private void configureRoutes(Router router) {
    router
        .route(GET, "/post")
        .respond(
            ctx ->
                ctx.response()
                    .putHeader("Content-Type", "text/html")
                    .end(
                        """
                        <!doctype html>
                        <html>
                          <head>
                            <meta charset="utf-8">
                            <title>Hello World!</title>
                          </head>
                          <body>
                            <h1>Hello, World!</h1>
                            <p>Hello, World!</p>
                          </body>
                        </html>
                        """));
  }

  private void defaultHandler(RoutingContext ctx) {
    ctx.response().putHeader("content-type", "text/plain").end("Hello from Vert.x!");
  }

  public int port() {
    if (port == -1) {
      throw new IllegalStateException("The verticle has not started");
    }
    return port;
  }

  private HttpServerOptions httpServerOptions(MainConfig config) {
    return new HttpServerOptions()
        .setUseAlpn(true)
        .setSsl(true)
        .setKeyStoreOptions(
            new JksOptions().setPath(config.keystore.path).setPassword(config.keystore.password));
  }
}
