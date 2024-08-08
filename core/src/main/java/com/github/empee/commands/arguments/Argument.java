package com.github.empee.commands.arguments;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.exceptions.ArgumentException;
import com.github.empee.commands.suggestions.CommandSuggestion;
import com.github.empee.commands.suggestions.SuggestionContext;
import com.github.empee.commands.suggestions.SuggestionType;
import com.github.empee.commands.utils.CommandReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class Argument<T> {

  private final String id;

  @Getter
  private final ArgumentParser<T> parser;

  @Getter @Nullable
  private Function<CommandContext<?>, ?> executor;

  @Getter @Nullable
  private Function<CommandContext<?>, List<CommandSuggestion>> suggestions;

  @Getter @Nullable
  private Function<CommandContext<?>, T> defaultValue;

  @Getter @Nullable
  private SuggestionType suggestionType;


  public static <E> Argument<E> arg(String id, ArgumentParser<E> parser) {
    Argument<E> arg = new Argument<>(id, parser);

    arg.withSuggestions(parser.getSuggestions());
    arg.withDefaultValue(parser.getDefaultValue());
    arg.withSuggestionType(parser.getSuggestionType());

    return arg;
  }

  @NotNull
  public T parse(CommandContext<?> context) {
    CommandReader cmdReader = context.getReader();
    if (cmdReader.hasReachedEnd()) {
      T parsedArg = getDefaultValue(context);
      if (parsedArg == null) {
        throw ArgumentException.notFound(parser);
      }

      return parsedArg;
    }

    String input = parser.read(cmdReader);

    try {
      return parser.parse(context, input);
    } catch (Exception e) {
      if (e instanceof ArgumentException) {
        throw e;
      }

      throw ArgumentException.parsing(parser, input, e);
    }
  }


  public Argument<T> withExecutor(Function<CommandContext<?>, ?> executor) {
    this.executor = executor;
    return this;
  }

  public Argument<T> withSuggestions(String... values) {
    suggestions = sender -> Stream.of(values)
        .map(v -> CommandSuggestion.of(v, null))
        .collect(Collectors.toList());

    return this;
  }

  public Argument<T> withSuggestions(Function<CommandContext<?>, List<CommandSuggestion>> value) {
    suggestions = value;
    return this;
  }

  public Argument<T> withDefaultValue(Function<CommandContext<?>, T> value) {
    defaultValue = value;
    return this;
  }

  public Argument<T> withSuggestionType(SuggestionType value) {
    suggestionType = value;
    return this;
  }

  @Nullable
  public SuggestionContext getSuggestions(CommandContext<?> context) {
    if (getSuggestions() == null) {
      return null;
    }

    var reader = context.getReader();

    var startIndex = reader.getCursor() + 2; //Start suggesting after the separator
    var input = reader.readRemaining();

    var suggestions = getSuggestions().apply(context);
    suggestions = suggestions.stream()
        .filter(s -> s.matches(input))
        .sorted().collect(Collectors.toList());

    return new SuggestionContext(startIndex, input.length(), suggestions);
  }

  @Nullable
  public T getDefaultValue(CommandContext<?> context) {
    var supplier = getDefaultValue();
    if (supplier == null) {
      return null;
    }

    return supplier.apply(context);
  }

}
