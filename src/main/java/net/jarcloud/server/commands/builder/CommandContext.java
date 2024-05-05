package net.jarcloud.server.commands.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minestom.server.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(staticName = "of")
public class CommandContext {

  @Getter
  private final CommandReader reader;
  private final CommandSender sender;
  private final Map<String, Object> context = new HashMap<>();

  public void set(String key, Object value) {
    context.put(key, value);
  }

  public <T> T get(String key) {
    return (T) context.get(key);
  }

  public CommandSender sender() {
    return sender;
  }

}
