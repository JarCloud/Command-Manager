package com.github.empee.commands.minestom;

import net.jarcloud.server.commands.arguments.properties.ArgumentProperties;
import net.jarcloud.server.commands.arguments.properties.DoubleProperties;
import net.jarcloud.server.commands.arguments.properties.IntegerProperties;
import net.jarcloud.server.commands.arguments.properties.StringProperties;
import net.minestom.server.utils.binary.BinaryWriter;

import java.util.HashMap;
import java.util.Map;

public class PropertiesMapper {

  private final Map<Class<? extends ArgumentProperties>, PropMapper<?>> mappers = new HashMap<>();

  public PropertiesMapper() {
    registerMapper(DoubleProperties.class, new DoubleMapper());
    registerMapper(IntegerProperties.class, new IntegerMapper());
    registerMapper(StringProperties.class, new StringMapper());
  }

  public interface PropMapper<T extends ArgumentProperties> {
    byte[] map(T t);
  }

  private static class DoubleMapper implements PropMapper<DoubleProperties> {
    public byte[] map(DoubleProperties prop) {
      return BinaryWriter.makeArray(packetWriter -> {
        byte flag = getFlag(prop);
        packetWriter.writeByte(flag);
        if (prop.getMin() != null) {
          packetWriter.writeDouble(prop.getMin());
        }

        if (prop.getMax() != null) {
          packetWriter.writeDouble(prop.getMax());
        }
      });
    }

    private static byte getFlag(DoubleProperties prop) {
      byte flag = 0;
      if (prop.getMax() != null) {
        flag |= 0x1;
      }

      if (prop.getMax() != null) {
        flag |= 0x2;
      }
      return flag;
    }
  }

  private static class IntegerMapper implements PropMapper<IntegerProperties> {
    public byte[] map(IntegerProperties prop) {
      return BinaryWriter.makeArray(packetWriter -> {
        byte flag = getFlag(prop);
        packetWriter.writeByte(flag);

        if (prop.getMin() != null) {
          packetWriter.writeInt(prop.getMin());
        }

        if (prop.getMax() != null) {
          packetWriter.writeInt(prop.getMax());
        }
      });

    }

    private static byte getFlag(IntegerProperties prop) {
      byte flag = 0;
      if (prop.getMax() != null) {
        flag |= 0x1;
      }

      if (prop.getMax() != null) {
        flag |= 0x2;
      }
      return flag;
    }
  }

  private static class StringMapper implements PropMapper<StringProperties> {
    public byte[] map(StringProperties prop) {
      return BinaryWriter.makeArray(packetWriter -> packetWriter.writeVarInt(prop.getType()));
    }
  }

  public <T extends ArgumentProperties> void registerMapper(Class<T> clazz, PropMapper<T> mapper) {
    mappers.put(clazz, mapper);
  }

  public <T extends ArgumentProperties> byte[] mapToByteArray(T properties) {
    PropMapper<T> mapper = (PropMapper<T>) mappers.get(properties.getClass());
    if (mapper == null) {
      throw new IllegalArgumentException("Can't find mapper for " + properties.getClass());
    }

    return mapper.map(properties);
  }

}
