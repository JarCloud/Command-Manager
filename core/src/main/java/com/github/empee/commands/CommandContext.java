package com.github.empee.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.github.empee.commands.exceptions.ArgumentException;
import com.github.empee.commands.utils.CommandReader;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(staticName = "of")
public class CommandContext<S> {

  @Getter private final CommandReader reader;
  @Getter private final S source;

  private final Map<String, Object> context = new HashMap<>();

  public void set(String key, Object value) {
    context.put(key, value);
  }

  public <T> T get(String key) {
    return (T) context.get(key);
  }

  public <K> K getSourceAs(Class<K> clazz) {
    if (clazz.isInstance(source)) {
      return (K) source;
    }

    throw ArgumentException.illegalSender();
  }

}
