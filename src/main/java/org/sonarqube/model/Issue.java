package org.sonarqube.model;

import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class Issue {

  private String key;
  private String component;
  private String project;
  private String rule;
  private String status;
  private String resolution;
  private String severity;
  private String message;
  private int line;
  private TextRange textRange;
  private String author;
  private String effort;
  private Date creationDate;
  private Date updateDate;
  private List<String> tags;
  private String type;
}
