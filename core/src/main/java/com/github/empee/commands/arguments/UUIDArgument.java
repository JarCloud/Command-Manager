package com.github.empee.commands.arguments;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.exceptions.ArgumentException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.github.empee.commands.suggestions.CommandSuggestion;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class UUIDArgument implements Argument<UUID> {

  public static final String ERROR_SYNTAX = "uuid_syntax_error";

  private final String id;
  private Function<CommandContext<?>, List<CommandSuggestion>> suggestions;
  private Function<CommandContext<?>, UUID> defaultValue;
  private Function<CommandContext<?>, ?> executor;

  public String getParser() {
    return "minecraft:uuid";
  }

  public UUID parse(CommandContext<?> context, String input) {
    try {
      return UUID.fromString(input);
    } catch (IllegalArgumentException e) {
      throw ArgumentException.parsing(this, input, ERROR_SYNTAX);
    }
  }

  public UUIDArgument withExecutor(Function<CommandContext<?>, ?> executor) {
    this.executor = executor;
    return this;
  }
  public UUIDArgument withExecutor(Consumer<CommandContext<?>> executor) {
    return withExecutor((ctx) -> {
      executor.accept(ctx);
      return null;
    });
  }

  public UUIDArgument withSuggestions(String... values) {
    suggestions = sender -> Stream.of(values)
        .map(v -> CommandSuggestion.of(v, null))
        .collect(Collectors.toList());

    return this;
  }
  public UUIDArgument withSuggestions(Function<CommandContext<?>, List<CommandSuggestion>> value) {
    suggestions = value;
    return this;
  }
  public UUIDArgument withDefaultValue(Function<CommandContext<?>, UUID> value) {
    defaultValue = value;
    return this;
  }
}
