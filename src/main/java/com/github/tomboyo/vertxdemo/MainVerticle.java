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
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class MainVerticle extends AbstractVerticle {

  private int port = -1;

  private PgPool pgPool;

  @Override
  public void start(Promise<Void> startPromise) {
    configure()
        .onSuccess(this::initPgPool)
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

  private void initPgPool(MainConfig config) {
    pgPool = PgPool.pool(vertx, PgConnectOptions.fromUri(config.postgres.uri), new PoolOptions());
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
    var postController = new PostController(pgPool);
    router.route(GET, "/posts/:id").handler(postController::handleGetPost);
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
