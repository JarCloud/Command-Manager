package com.github.empee.commands.arguments;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.properties.ArgumentProperties;
import com.github.empee.commands.exceptions.ArgumentException;
import com.github.empee.commands.suggestions.CommandSuggestion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class UuidParser implements ArgumentParser<UUID> {

  public static final String ERROR_SYNTAX = "uuid_syntax_error";

  private final Function<CommandContext<?>, List<CommandSuggestion>> suggestions = null;
  private final Function<CommandContext<?>, UUID> defaultValue = null;

  public UUID parse(CommandContext<?> context, String input) {
    try {
      return UUID.fromString(input);
    } catch (IllegalArgumentException e) {
      throw ArgumentException.parsing(this, input, ERROR_SYNTAX);
    }
  }

  public ArgumentProperties getProperties() {
    return null;
  }

}
