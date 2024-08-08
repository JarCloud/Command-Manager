package com.github.empee.commands.arguments.numbers;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.ArgumentParser;
import com.github.empee.commands.arguments.properties.IntegerProperties;
import com.github.empee.commands.exceptions.ArgumentException;
import com.github.empee.commands.suggestions.CommandSuggestion;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@Getter
public class IntegerParser implements ArgumentParser<Integer> {

  public static final String ERROR_NOT_AN_INTEGER = "int_not_valid";
  public static final String ERROR_TOO_HIGH = "number_too_high";
  public static final String ERROR_TOO_LOW = "number_too_low";

  private final Function<CommandContext<?>, List<CommandSuggestion>> suggestions = null;
  private final Function<CommandContext<?>, Integer> defaultValue = null;

  private Integer min;
  private Integer max;

  public @NotNull Integer parse(CommandContext<?> context, String input) {
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

  public IntegerProperties getProperties() {
    return IntegerProperties.builder()
        .min(min)
        .max(max)
        .build();
  }

  public IntegerParser withMin(int min) {
    this.min = min;
    return this;
  }
  public IntegerParser withMax(int max) {
    this.max = max;
    return this;
  }

}
