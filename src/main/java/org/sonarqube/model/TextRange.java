package org.sonarqube.model;

import lombok.Getter;

@Getter
public class TextRange {
  private int startLine;
  private int endLine;
  private int startOffset;
  private int endOffset;
}
