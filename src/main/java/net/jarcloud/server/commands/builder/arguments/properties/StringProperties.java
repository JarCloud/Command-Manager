package net.jarcloud.server.commands.builder.arguments.properties;

import lombok.RequiredArgsConstructor;
import net.minestom.server.utils.binary.BinaryWriter;

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

  @Override
  public byte[] get() {
    return BinaryWriter.makeArray(packetWriter -> packetWriter.writeVarInt(type));
  }
}
