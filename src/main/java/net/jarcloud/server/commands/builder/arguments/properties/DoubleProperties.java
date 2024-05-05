package net.jarcloud.server.commands.builder.arguments.properties;

import lombok.Builder;
import net.minestom.server.utils.binary.BinaryWriter;

@Builder
public class DoubleProperties implements ArgumentProperties {

  private Double min;
  private Double max;

  private byte getFlag() {
    byte result = 0;
    if (min != null) {
      result |= 0x1;
    }

    if (max != null) {
      result |= 0x2;
    }

    return result;
  }

  @Override
  public byte[] get() {
    return BinaryWriter.makeArray(packetWriter -> {
      packetWriter.writeByte(getFlag());
      if (min != null) {
        packetWriter.writeDouble(min);
      }

      if (max != null) {
        packetWriter.writeDouble(max);
      }
    });
  }
}
