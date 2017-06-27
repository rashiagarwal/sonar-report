package org.sonarqube;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Builder {

  static Retrofit build() {
    return new Retrofit.Builder().baseUrl("http://1plurdctsn01.main.usfood.com:9000")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }
}