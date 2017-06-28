package org.sonarqube;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Sonar {

  static Retrofit createConnection() {
    String url = System.getProperty("url");

    return new Retrofit.Builder().baseUrl(url)
      .addConverterFactory(GsonConverterFactory.create())
      .build();
  }
}