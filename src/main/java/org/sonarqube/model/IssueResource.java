package org.sonarqube.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Set;

@Getter
public class IssueResource {

  @JsonProperty("paging")
  Paging paging;

  @JsonProperty("issues")
  Set<Issue> issues;
}
