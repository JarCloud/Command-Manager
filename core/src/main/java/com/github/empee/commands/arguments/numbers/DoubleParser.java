package com.github.empee.commands.arguments.numbers;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.ArgumentParser;
import com.github.empee.commands.arguments.properties.DoubleProperties;
import com.github.empee.commands.exceptions.ArgumentException;
import com.github.empee.commands.suggestions.CommandSuggestion;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@Getter
public class DoubleParser implements ArgumentParser<Double> {

  public static final String ERROR_NOT_A_DOUBLE = "double_not_valid";
  public static final String ERROR_TOO_HIGH = "number_too_high";
  public static final String ERROR_TOO_LOW = "number_too_low";

  private final Function<CommandContext<?>, List<CommandSuggestion>> suggestions = null;
  private final Function<CommandContext<?>, Double> defaultValue = null;

  private Double min;
  private Double max;

  public @NotNull Double parse(CommandContext<?> context, String input) {
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

  public DoubleProperties getProperties() {
    return DoubleProperties.builder()
        .min(min)
        .max(max)
        .build();
  }

  public DoubleParser withMin(double min) {
    this.min = min;
    return this;
  }
  public DoubleParser withMax(double max) {
    this.max = max;
    return this;
  }

}
