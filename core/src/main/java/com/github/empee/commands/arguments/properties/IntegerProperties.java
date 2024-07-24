package com.github.empee.commands.arguments.properties;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IntegerProperties implements ArgumentProperties {

  private Integer min;
  private Integer max;

}
