package net.jarcloud.server.commands.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class CommandSuggestion implements Comparable<CommandSuggestion> {
  private final String value;
  private final Component tooltip;

  public boolean matches(String input) {
    return value.toLowerCase().startsWith(input.toLowerCase());
  }

  @Override
  public int compareTo(@NotNull CommandSuggestion o) {
    return value.compareTo(o.getValue());
  }
}
