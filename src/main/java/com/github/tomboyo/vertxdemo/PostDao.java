package com.github.tomboyo.vertxdemo;

import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import java.util.Optional;

public class PostDao {
  public static Future<Optional<Post>> getPostById(PgPool pgPool, int postId) {
    return pgPool
        .getConnection()
        .compose(
            conn ->
                conn.preparedQuery("select * from posts where id = $1")
                    .execute(Tuple.of(postId))
                    .map(PostDao::zeroOrOne)
                    .eventually(v -> conn.close()));
  }

  private static Optional<Post> zeroOrOne(RowSet<Row> rowSet) {
    return switch (rowSet.rowCount()) {
      case 0 -> Optional.empty();
      case 1 -> Optional.of(toPost(rowSet.iterator().next()));
      default -> throw new IllegalStateException("Expected 0 or 1 rows");
    };
  }

  private static Post toPost(Row row) {
    return new Post(row.get(Integer.class, "id"), row.get(String.class, "content"));
  }
}
