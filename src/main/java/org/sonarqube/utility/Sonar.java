package org.sonarqube.utility;

import org.apache.log4j.Logger;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Sonar {

  private static final Logger logger = Logger.getLogger("Sonar Connectivity ");

  private Sonar() {
  }

  public static Retrofit createConnection(String url) {
    try {
//      String url = System.getProperty("url");

      return new Retrofit.Builder().baseUrl(url)
          .addConverterFactory(GsonConverterFactory.create())
          .build();
    } catch (NullPointerException | IllegalArgumentException ex) {
      logger.error(ex.getMessage());
    }
    return null;
  }
}