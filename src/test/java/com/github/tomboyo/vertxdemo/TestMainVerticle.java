package com.github.tomboyo.vertxdemo;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import static io.vertx.core.http.HttpMethod.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  private MainVerticle verticle;

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext testContext) throws Exception {
    verticle = new MainVerticle();
    vertx.deployVerticle(
        verticle,
        new DeploymentOptions().setConfig(
            new JsonObject()
                .put("main.verticle.http.port", 0)
                .put("main.verticle.p12.path", "etc/certs/server.p12")),
        testContext.succeedingThenComplete());
  }

  @Test
  void sayHello(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.createHttpClient(new HttpClientOptions()
        .setPemTrustOptions(new PemTrustOptions()
            .addCertPath("etc/certs/cert.pem")))
        .request(new RequestOptions()
            .setSsl(true)
            .setPort(verticle.port())
            .setHost("localhost")
            .setMethod(GET)
            .setURI("/"))
        .compose(req -> req.send().compose(HttpClientResponse::body))
        .onComplete(testContext.succeeding(buffer -> testContext.verify(() -> {
          assertEquals("Hello from Vert.x!", buffer.toString());
          testContext.completeNow();
        })));
  }
}
