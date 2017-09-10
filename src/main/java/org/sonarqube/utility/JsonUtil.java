package org.sonarqube.utility;

import com.google.gson.*;
import spark.ResponseTransformer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class JsonUtil {

  private JsonUtil() {
  }

  public static <T> T deSerialize(String body, Class<T> classOfT) {
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
      Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
      return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }).create();
    return gson.fromJson(body, classOfT);
  }

  public static <T> String serialize(T object) {
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toInstant(ZoneOffset.UTC).toEpochMilli())).create();
    return gson.toJson(object);
  }

  public static ResponseTransformer json() {
    return JsonUtil::toJson;
  }

  public static String toJson(Object object) {
    return new Gson().toJson(object);
  }
}
