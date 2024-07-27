package com.github.empee.commands.arguments;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.properties.StringProperties;
import com.github.empee.commands.suggestions.CommandSuggestion;
import com.github.empee.commands.utils.CommandReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class StringArgument implements Argument<String> {

  private final String id;
  private Function<CommandContext<?>, List<CommandSuggestion>> suggestions;
  private Function<CommandContext<?>, String> defaultValue;
  private Function<CommandContext<?>, ?> executor;

  private boolean greedy;
  private boolean quotable;

  public StringArgument greedy() {
    this.greedy = true;
    return this;
  }

  public StringArgument quotable() {
    this.quotable = true;
    return this;
  }

  public String getParser() {
    return "brigadier:string";
  }

  public @NotNull String parse(CommandContext<?> context, String input) {
    return input;
  }

  @Override
  public @NotNull String read(CommandReader reader) {
    if (greedy) {
      return reader.readRemaining();
    } else if (quotable) {
      return reader.read();
    } else {
      return reader.readUnquoted();
    }
  }

  @Override
  public StringProperties getProperties() {
    if (greedy) {
      return StringProperties.greedy();
    }

    if (quotable) {
      return StringProperties.quotable();
    }

    return StringProperties.word();
  }

  public StringArgument withExecutor(Function<CommandContext<?>, ?> executor) {
    this.executor = executor;
    return this;
  }
  public StringArgument withExecutor(Consumer<CommandContext<?>> executor) {
    return withExecutor((ctx) -> {
      executor.accept(ctx);
      return null;
    });
  }

  public StringArgument withSuggestions(String... values) {
    suggestions = sender -> Stream.of(values)
        .map(v -> CommandSuggestion.of(v, null))
        .collect(Collectors.toList());

    return this;
  }
  public StringArgument withSuggestions(Function<CommandContext<?>, List<CommandSuggestion>> value) {
    suggestions = value;
    return this;
  }
  public StringArgument withDefaultValue(Function<CommandContext<?>, String> value) {
    defaultValue = value;
    return this;
  }
}
