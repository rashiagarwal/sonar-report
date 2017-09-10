package org.sonarqube.utility;

import org.junit.jupiter.api.Test;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class SonarTest {

  @Test
  void shouldBeWellDefined() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
    InvocationTargetException {

    assertUtilityClassWellDefined(Sonar.class);
  }

  @Test
  void shouldReturnNullWhenUrlIsNotProvided() {
    assertNull(Sonar.createConnection(""), "Connection should be null when url is not provided");
  }

  @Test
  void shouldReturnNullWhenUrlIsIllegal() {
    System.setProperty("url", "illegal-url");

    assertNull(Sonar.createConnection(""), "Connection should be null when url is illegal");
  }

  @Test
  void shouldReturnConnectionWhenUrlIsLegal() {
    System.setProperty("url", "http://legal-url.com");

    Retrofit connection = Sonar.createConnection("http://legal-url.com");

    assertEquals("http://legal-url.com/", connection.baseUrl().url().toString());
    assertTrue(connection.converterFactories().get(1) instanceof GsonConverterFactory);

  }

  private void assertUtilityClassWellDefined(
    final Class<?> clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException,
    InvocationTargetException {


    assertAll("Utility class should pass all the assertions",
      () -> assertTrue(Modifier.isFinal(clazz.getModifiers()), "class must be final"),
      () -> assertEquals(1, clazz.getDeclaredConstructors().length, "There must be only one constructor"),
      () -> {
        final Constructor<?> constructor = clazz.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
          fail("constructor is not private");
        }
      },
      () -> {
        for (final Method method : clazz.getMethods()) {
          if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)) {
            fail("there exists a non-static method:" + method);
          }
        }
      }
    );
  }
}