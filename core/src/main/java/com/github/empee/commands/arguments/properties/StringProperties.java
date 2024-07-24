package com.github.empee.commands.arguments.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StringProperties implements ArgumentProperties {

  public static final int GREEDY = 2;
  public static final int QUOTABLE = 1;
  public static final int WORD = 0;

  private final int type;

  public static StringProperties greedy() {
    return new StringProperties(GREEDY);
  }

  public static StringProperties quotable() {
    return new StringProperties(QUOTABLE);
  }

  public static StringProperties word() {
    return new StringProperties(WORD);
  }
}
