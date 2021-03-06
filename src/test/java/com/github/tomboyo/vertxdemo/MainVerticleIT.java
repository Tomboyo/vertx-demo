package com.github.tomboyo.vertxdemo;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.vertx.core.http.HttpMethod.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class MainVerticleIT {

  private MainVerticle verticle;

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    verticle = new MainVerticle();
    vertx.deployVerticle(
        verticle,
        new DeploymentOptions()
            .setConfig(
                new JsonObject()
                    .put("port", 0)
                    .put("keystore", new JsonObject().put("path", "conf/certs/server.p12"))),
        testContext.succeedingThenComplete());
  }

  @Test
  void getPost(Vertx vertx, VertxTestContext testContext) {
    vertx
        .createHttpClient(
            new HttpClientOptions()
                .setPemTrustOptions(new PemTrustOptions().addCertPath("conf/certs/cert.pem")))
        .request(
            new RequestOptions()
                .setSsl(true)
                .setPort(verticle.port())
                .setHost("localhost")
                .setMethod(GET)
                .setURI("/posts/1"))
        .compose(HttpClientRequest::send)
        .onComplete(
            testContext.succeeding(
                resp -> {
                  assertEquals(200, resp.statusCode(), "Should return the default post with id 1");
                  testContext.completeNow();
                }));
  }
}
