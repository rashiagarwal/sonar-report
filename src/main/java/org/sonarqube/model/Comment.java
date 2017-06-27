package org.sonarqube.model;

import lombok.Getter;

import java.util.Date;

@Getter
public class Comment {

  private String key;
  private String login;
  private String htmlText;
  private String markdown;
  private boolean updatable;
  private Date createdAt;
}
