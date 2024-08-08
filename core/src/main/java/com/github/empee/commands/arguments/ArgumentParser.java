package com.github.empee.commands.arguments;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.properties.ArgumentProperties;
import com.github.empee.commands.suggestions.CommandSuggestion;
import com.github.empee.commands.suggestions.SuggestionType;
import com.github.empee.commands.utils.CommandReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public interface ArgumentParser<T> {

  @NotNull T parse(CommandContext<?> context, String input);

  @Nullable
  Function<CommandContext<?>, List<CommandSuggestion>> getSuggestions();

  @Nullable Function<CommandContext<?>, T> getDefaultValue();

  @Nullable ArgumentProperties getProperties();

  @NotNull default String read(CommandReader reader) {
    return reader.readUnquoted();
  }

  @Nullable default SuggestionType getSuggestionType() {
    if (getSuggestions() != null) {
      return SuggestionType.ASK_SERVER;
    }

    return null;
  }

}