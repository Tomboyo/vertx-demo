package com.github.tomboyo.vertxdemo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Structured configuration object for MainVerticle. Fields are public for convenience but should
 * never be mutated by application code.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MainConfig {
  public int port = 443;
  public Keystore keystore = new Keystore();
  public Postgres postgres = new Postgres();

  public static class Keystore {
    public String path = "conf/certs/server.p12";
    public String password;
  }

  public static class Postgres {
    public String uri;
  }
}
