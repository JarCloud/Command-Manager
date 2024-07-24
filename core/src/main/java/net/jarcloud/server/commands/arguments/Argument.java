package net.jarcloud.server.commands.arguments;

import net.jarcloud.server.commands.CommandContext;
import net.jarcloud.server.commands.suggestions.SuggestionType;
import net.jarcloud.server.commands.arguments.properties.ArgumentProperties;
import net.jarcloud.server.commands.utils.CommandReader;
import net.jarcloud.server.commands.suggestions.CommandSuggestion;
import net.jarcloud.server.commands.suggestions.SuggestionContext;
import net.jarcloud.server.commands.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Argument<T> {

  @NotNull String getId();
  @NotNull String getParser();

  @NotNull T parse(CommandContext<?> context, String input);

  @Nullable Function<CommandContext<?>, ?> getExecutor();
  Argument<T> withExecutor(Function<CommandContext<?>, ?> executor);
  Argument<T> withExecutor(Consumer<CommandContext<?>> executor);

  @Nullable Function<CommandContext<?>, List<CommandSuggestion>> getSuggestions();

  default @Nullable Function<CommandContext<?>, T> getDefaultValue() {
    return null;
  }

  default @Nullable SuggestionContext getSuggestions(CommandContext<?> context) {
    if (getSuggestions() == null) {
      return null;
    }

    var reader = context.getReader();
    var start = reader.getCursor() + 2; //Start after the separator
    var input = read(reader);

    var suggestions = getSuggestions().apply((CommandContext<Object>) context);
    suggestions = suggestions.stream()
        .filter(s -> s.matches(input))
        .sorted().collect(Collectors.toList());

    return new SuggestionContext(start, input.length(), suggestions);
  }

  default @Nullable ArgumentProperties getProperties() {
    return null;
  }

  @NotNull default T parse(CommandContext<?> context) {
    CommandReader cmdReader = context.getReader();
    if (cmdReader.hasReachedEnd()) {
      T parsedArg = getDefaultValue(context);
      if (parsedArg == null) {
        throw ArgumentException.notFound(this);
      }

      return parsedArg;
    }

    String rawArg = read(cmdReader);

    try {
      return parse(context, rawArg);
    } catch (Exception e) {
      if (e instanceof ArgumentException) {
        throw e;
      }

      throw ArgumentException.parsing(this, rawArg, e);
    }
  }

  @NotNull default String read(CommandReader reader) {
    return reader.readUnquoted();
  }

  @Nullable default SuggestionType getSuggestionType() {
    if (getSuggestions() != null) {
      return SuggestionType.ASK_SERVER;
    }

    return null;
  }

  @Nullable default T getDefaultValue(CommandContext<?> context) {
    var supplier = getDefaultValue();
    if (supplier == null) {
      return null;
    }

    return supplier.apply((CommandContext<Object>) context);
  }
}
