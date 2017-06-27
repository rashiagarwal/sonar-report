package org.sonarqube.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Set;

@Getter
public class TagResource {

  @JsonProperty("tags")
  Set<String> tags;
}
