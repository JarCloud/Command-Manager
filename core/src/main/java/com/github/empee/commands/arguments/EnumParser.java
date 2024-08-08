package com.github.empee.commands.arguments;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.properties.StringProperties;
import com.github.empee.commands.exceptions.ArgumentException;
import com.github.empee.commands.suggestions.CommandSuggestion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class EnumParser<E extends Enum<E>> implements ArgumentParser<E> {

  public static final String ERROR_NOT_FOUND = "enum_not_found";

  private final Class<E> enumClass;

  private final Function<CommandContext<?>, List<CommandSuggestion>> suggestions = sender -> {
    return Arrays.stream(getEnumClass().getEnumConstants())
        .map(e -> CommandSuggestion.of(e.name(), null))
        .collect(Collectors.toList());
  };

  private final Function<CommandContext<?>, E> defaultValue = null;

  public E parse(CommandContext<?> context, String input) {
    try {
      return Enum.valueOf(enumClass, input.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw ArgumentException.parsing(this, input, ERROR_NOT_FOUND);
    }
  }

  public StringProperties getProperties() {
    return StringProperties.word();
  }

}
