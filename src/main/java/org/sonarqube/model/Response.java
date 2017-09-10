package org.sonarqube.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response {
  private String status;

  @SerializedName("response")
  private Object body;
}
