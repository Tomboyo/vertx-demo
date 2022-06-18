package com.github.tomboyo.vertxdemo;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;

public class PostController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

  private final PgPool pgPool;

  public PostController(PgPool pgPool) {
    this.pgPool = pgPool;
  }

  public void handleGetPost(RoutingContext ctx) {
    var postId = Integer.parseInt(ctx.pathParam("id"));
    PostDao.getPostById(pgPool, postId)
        .onSuccess(
            result ->
                ctx.response()
                    .putHeader("Content-Type", "text/html")
                    .setStatusCode(result.map(x -> 200).orElse(404))
                    .end(result.map(Post::content).orElse(notFound(postId))))
        .onFailure(
            t -> {
              LOGGER.error("Failed to retrieve post", t);
              ctx.response()
                  .putHeader("Content-Type", "text/html")
                  .setStatusCode(500)
                  .end("Unexpected error");
            });
  }

  private static String notFound(int postId) {
    return """
        <!doctype html>
                <html>
                  <head>
                    <meta charset="utf-8">
                    <title>Post Not Found</title>
                  </head>
                  <body>
                    <h1>Post :postId not found.</h1>
                  </body>
                </html>
        """
        .replaceAll(":postId", Integer.toString(postId));
  }
}
