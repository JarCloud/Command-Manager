package com.github.empee.commands.arguments;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.properties.StringProperties;
import com.github.empee.commands.suggestions.CommandSuggestion;
import com.github.empee.commands.utils.CommandReader;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@Getter
public class StringParser implements ArgumentParser<String> {

  private boolean greedy;
  private boolean quotable;

  private final Function<CommandContext<?>, List<CommandSuggestion>> suggestions = null;
  private final Function<CommandContext<?>, String> defaultValue = null;

  public StringParser greedy() {
    this.greedy = true;
    return this;
  }

  public StringParser quotable() {
    this.quotable = true;
    return this;
  }

  public String parse(CommandContext<?> context, String input) {
    return input;
  }

  public String read(CommandReader reader) {
    if (greedy) {
      return reader.readRemaining();
    } else if (quotable) {
      return reader.read();
    } else {
      return reader.readUnquoted();
    }
  }

  public StringProperties getProperties() {
    if (greedy) {
      return StringProperties.greedy();
    }

    if (quotable) {
      return StringProperties.quotable();
    }

    return StringProperties.word();
  }

}
