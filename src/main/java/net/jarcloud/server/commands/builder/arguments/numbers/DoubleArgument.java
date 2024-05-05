package net.jarcloud.server.commands.builder.arguments.numbers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jarcloud.server.commands.builder.CommandContext;
import net.jarcloud.server.commands.builder.CommandSuggestion;
import net.jarcloud.server.commands.builder.arguments.Argument;
import net.jarcloud.server.commands.builder.exceptions.ArgumentException;
import net.jarcloud.server.commands.builder.arguments.properties.DoubleProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class DoubleArgument implements Argument<Double> {

  public static final String ERROR_NOT_A_DOUBLE = "double_not_valid";
  public static final String ERROR_TOO_HIGH = "number_too_high";
  public static final String ERROR_TOO_LOW = "number_too_low";

  private final String id;
  private Function<CommandContext, List<CommandSuggestion>> suggestions;
  private Function<CommandContext, Double> defaultValue;
  private Function<CommandContext, ?> executor;

  private Double min;
  private Double max;

  public String getParser() {
    return "brigadier:double";
  }

  public @NotNull Double parse(CommandContext context, String input) {
    try {
      var result = Double.parseDouble(input);
      if (min != null && min > result) {
        throw ArgumentException.parsing(this, input, ERROR_TOO_LOW);
      }

      if (min != null && max < result) {
        throw ArgumentException.parsing(this, input, ERROR_TOO_HIGH);
      }

      return result;
    } catch (IllegalArgumentException e) {
      throw ArgumentException.parsing(this, input, ERROR_NOT_A_DOUBLE);
    }
  }

  @Override
  public DoubleProperties getProperties() {
    return DoubleProperties.builder()
        .min(min)
        .max(max)
        .build();
  }


  public DoubleArgument withMin(double min) {
    this.min = min;
    return this;
  }
  public DoubleArgument withMax(double max) {
    this.max = max;
    return this;
  }

  public DoubleArgument withExecutor(Function<CommandContext, ?> executor) {
    this.executor = executor;
    return this;
  }
  public DoubleArgument withExecutor(Consumer<CommandContext> executor) {
    return withExecutor((ctx) -> {
      executor.accept(ctx);
      return null;
    });
  }

  public DoubleArgument withSuggestions(String... values) {
    suggestions = sender -> Stream.of(values)
        .map(v -> CommandSuggestion.of(v, null))
        .toList();

    return this;
  }
  public DoubleArgument withSuggestions(Function<CommandContext, List<CommandSuggestion>> value) {
    suggestions = value;
    return this;
  }
  public DoubleArgument withDefaultValue(Function<CommandContext, Double> value) {
    defaultValue = value;
    return this;
  }
}
