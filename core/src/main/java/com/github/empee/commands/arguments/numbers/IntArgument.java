package com.github.empee.commands.arguments.numbers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.Argument;
import com.github.empee.commands.arguments.properties.IntegerProperties;
import com.github.empee.commands.suggestions.CommandSuggestion;
import com.github.empee.commands.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class IntArgument implements Argument<Integer> {

  public static final String ERROR_NOT_AN_INTEGER = "int_not_valid";
  public static final String ERROR_TOO_HIGH = "number_too_high";
  public static final String ERROR_TOO_LOW = "number_too_low";

  private final String id;
  private Function<CommandContext<?>, List<CommandSuggestion>> suggestions;
  private Function<CommandContext<?>, Integer> defaultValue;
  private Function<CommandContext<?>, ?> executor;

  private Integer min;
  private Integer max;

  public String getParser() {
    return "brigadier:integer";
  }

  public @NotNull Integer parse(CommandContext context, String input) {
    try {
      var result = Integer.parseInt(input);
      if (min != null && min > result) {
        throw ArgumentException.parsing(this, input, ERROR_TOO_LOW);
      }

      if (min != null && max < result) {
        throw ArgumentException.parsing(this, input, ERROR_TOO_HIGH);
      }

      return result;
    } catch (IllegalArgumentException e) {
      throw ArgumentException.parsing(this, input, ERROR_NOT_AN_INTEGER);
    }
  }

  @Override
  public IntegerProperties getProperties() {
    return IntegerProperties.builder()
        .min(min)
        .max(max)
        .build();
  }



  public IntArgument withMin(int min) {
    this.min = min;
    return this;
  }
  public IntArgument withMax(int max) {
    this.max = max;
    return this;
  }

  public IntArgument withExecutor(Function<CommandContext<?>, ?> executor) {
    this.executor = executor;
    return this;
  }
  public IntArgument withExecutor(Consumer<CommandContext<?>> executor) {
    return withExecutor((ctx) -> {
      executor.accept(ctx);
      return null;
    });
  }

  public IntArgument withSuggestions(String... values) {
    suggestions = sender -> Stream.of(values)
        .map(v -> CommandSuggestion.of(v, null))
        .collect(Collectors.toList());

    return this;
  }
  public IntArgument withSuggestions(Function<CommandContext<?>, List<CommandSuggestion>> value) {
    suggestions = value;
    return this;
  }
  public IntArgument withDefaultValue(Function<CommandContext<?>, Integer> value) {
    defaultValue = value;
    return this;
  }
}
