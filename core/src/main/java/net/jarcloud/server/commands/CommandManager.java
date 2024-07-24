package net.jarcloud.server.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.jarcloud.server.commands.exceptions.CommandException;
import net.jarcloud.server.commands.suggestions.SuggestionContext;
import net.jarcloud.server.commands.utils.CommandReader;

import java.util.function.BiConsumer;

public class CommandManager<S> {

  @Getter
  private final CommandNode<S> root;

  @Setter
  private BiConsumer<CommandContext<S>, CommandException> exceptionHandler = (ctx, e) -> {
    throw new RuntimeException("Error while executing command, type " + e.getType(), e);
  };

  public CommandManager(Class<S> sourceType) {
    root = CommandNode.of("", sourceType);
  }

  public void register(CommandNode<? extends S> command) {
    root.withChild(command);
  }

  public <T> T execute(S source, String input) {
    input = input.trim();

    CommandContext<S> context = CommandContext.of(new CommandReader(input), source);

    try {
      return root.execute(context);
    } catch (CommandException e) {
      exceptionHandler.accept(context, e);
    }

    return null;
  }

  public SuggestionContext getSuggestions(S source, String input) {
    return root.getSuggestions(CommandContext.of(new CommandReader(input), source));
  }

}
