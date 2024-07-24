package com.github.empee.commands.arguments.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StringProperties implements ArgumentProperties {

  private final int type;

  public static StringProperties greedy() {
    return new StringProperties(2);
  }

  public static StringProperties quotable() {
    return new StringProperties(1);
  }

  public static StringProperties word() {
    return new StringProperties(0);
  }
}
