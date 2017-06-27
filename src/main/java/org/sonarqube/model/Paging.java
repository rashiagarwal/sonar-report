package org.sonarqube.model;

import lombok.Getter;

@Getter
public class Paging {

  private int pageIndex;
  private int pageSize;
  private int total;
}
