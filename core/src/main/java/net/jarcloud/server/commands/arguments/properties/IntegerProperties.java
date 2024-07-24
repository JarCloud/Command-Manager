package net.jarcloud.server.commands.arguments.properties;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IntegerProperties implements ArgumentProperties {

  private Integer min;
  private Integer max;

}
