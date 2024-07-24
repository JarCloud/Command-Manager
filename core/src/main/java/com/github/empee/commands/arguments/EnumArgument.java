package com.github.empee.commands.arguments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.properties.StringProperties;
import com.github.empee.commands.suggestions.CommandSuggestion;
import com.github.empee.commands.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class EnumArgument<E extends Enum<E>> implements Argument<E> {

  public static final String ERROR_NOT_FOUND = "enum_not_found";

  private final String id;
  private final Class<E> enumClass;

  private Function<CommandContext<?>, E> defaultValue;
  private Function<CommandContext<?>, ?> executor;
  private Function<CommandContext<?>, List<CommandSuggestion>> suggestions = sender -> {
    return Arrays.stream(getEnumClass().getEnumConstants())
        .map(e -> CommandSuggestion.of(e.name(), null))
        .collect(Collectors.toList());
  };

  public String getParser() {
    return "brigadier:string";
  }

  public @NotNull E parse(CommandContext<?> context, String input) {
    try {
      return Enum.valueOf(enumClass, input.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw ArgumentException.parsing(this, input, ERROR_NOT_FOUND);
    }
  }

  @Override
  public StringProperties getProperties() {
    return StringProperties.word();
  }

  public EnumArgument<E> withExecutor(Function<CommandContext<?>, ?> executor) {
    this.executor = executor;
    return this;
  }
  public EnumArgument<E> withExecutor(Consumer<CommandContext<?>> executor) {
    return withExecutor((ctx) -> {
      executor.accept(ctx);
      return null;
    });
  }

  public EnumArgument<E> withDefaultValue(Function<CommandContext<?>, E> value) {
    defaultValue = value;
    return this;
  }
}
