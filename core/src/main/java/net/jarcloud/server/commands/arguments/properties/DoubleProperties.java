package net.jarcloud.server.commands.arguments.properties;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoubleProperties implements ArgumentProperties {

  private Double min;
  private Double max;

}
