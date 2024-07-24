package net.jarcloud.server.commands.suggestions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class CommandSuggestion implements Comparable<CommandSuggestion> {
  private final String value;

  @Nullable
  private final String tooltip;

  public boolean matches(String input) {
    return value.toLowerCase().startsWith(input.toLowerCase());
  }

  public Optional<String> getTooltip() {
    return Optional.ofNullable(tooltip);
  }

  @Override
  public int compareTo(@NotNull CommandSuggestion o) {
    return value.compareTo(o.getValue());
  }
}
