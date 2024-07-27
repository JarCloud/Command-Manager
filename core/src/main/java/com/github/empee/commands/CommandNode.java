package com.github.empee.commands;

import com.github.empee.commands.arguments.Argument;
import com.github.empee.commands.exceptions.CommandException;
import com.github.empee.commands.suggestions.SuggestionContext;
import com.github.empee.commands.utils.CommandReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class CommandNode<S> {

  private final String literal;
  private final Class<S> sourceType;

  private final List<CommandNode<?>> children = new ArrayList<>();
  private final List<Argument<?>> arguments = new ArrayList<>();
  private final List<CommandNode<S>> aliases = new ArrayList<>();

  private CommandNode<S> redirectNode;
  private Function<CommandContext<S>, ?> defaultExecutor;
  private Function<S, ? extends CommandException> accessTester;

  public CommandNode<S> withChild(CommandNode<? extends S> child) {
    if(findChild(child.literal).isPresent()) {
      throw new IllegalArgumentException("Node '" + child.literal + "' already registered!");
    }

    children.add(child);
    children.addAll(child.aliases);

    return this;
  }

  public CommandNode<S> withArgs(Argument<?>... args) {
    arguments.addAll(Arrays.asList(args));
    return this;
  }

  public CommandNode<S> withDefaultExecutor(Function<CommandContext<S>, ?> executable) {
    defaultExecutor = executable;

    return this;
  }

  public CommandNode<S> withDefaultExecutor(Consumer<CommandContext<S>> executable) {
    return withDefaultExecutor(ctx -> {
      executable.accept(ctx);
      return null;
    });
  }

  public CommandNode<S> withExecutor(Function<CommandContext<S>, ?> executable) {
    if (arguments.isEmpty()) {
      defaultExecutor = executable;
    } else {
      arguments.get(arguments.size() - 1).withExecutor((Function) executable);
    }

    return this;
  }

  public CommandNode<S> withExecutor(Consumer<CommandContext<S>> executable) {
    return withExecutor(ctx -> {
      executable.accept(ctx);
      return null;
    });
  }

  public CommandNode<S> withAccessTester(Function<S, ? extends CommandException> accessTester) {
    this.accessTester = accessTester;
    return this;
  }

  public CommandNode<S> withPermission(Predicate<S> accessTester) {
    this.accessTester = sender -> accessTester.test(sender) ? null : CommandException.missingPermission();
    return this;
  }

  public CommandNode<S> withAliases(String... aliases) {
    for (String alias : aliases) {
      var node = CommandNode.of(alias, sourceType);
      node.redirectNode = this;
      this.aliases.add(node);
    }

    return this;
  }

  public Optional<CommandNode<?>> findChild(String literal) {
    return children.stream()
        .filter(c -> c.getLiteral().equalsIgnoreCase(literal))
        .findAny();
  }

  @Nullable
  public SuggestionContext getSuggestions(CommandContext<S> context) {
    if (redirectNode != null) {
      return redirectNode.getSuggestions(context);
    }

    if (accessTester != null) {
      var result = accessTester.apply(context.getSource());
      if (result != null) {
        return null;
      }
    }

    var reader = context.getReader();
    if (reader.hasReachedEnd()) {
      return null;
    }

    for (Argument<?> argument : arguments) {
      var raw = argument.read(reader);
      if (!reader.canRead()) {
        reader.back();
        return argument.getSuggestions(context);
      }

      try {
        context.set(argument.getId(), argument.parse(context, raw));
      } catch (Exception e) {
        return null;
      }
    }

    CommandNode<?> child = findChild(reader.readUnquoted()).orElse(null);
    if (child == null) {
      return null;
    }

    if (!child.getSourceType().isInstance(context.getSource())) {
      return null;
    }

    return ((CommandNode<S>) child).getSuggestions(context);
  }

  public <T> T execute(CommandContext<S> context) {
    if (redirectNode != null) {
      return redirectNode.execute(context);
    }

    if (accessTester != null) {
      var result = accessTester.apply(context.getSource());
      if (result != null) {
        throw result;
      }
    }

    CommandReader cmdReader = context.getReader();
    if (cmdReader.hasReachedEnd()) {
      return (T) execute(context, (Function) defaultExecutor);
    }

    for (Argument<?> argument : arguments) {
      context.set(argument.getId(), argument.parse(context));
    }

    if (cmdReader.hasReachedEnd()) {
      return execute(context, arguments.get(arguments.size() - 1).getExecutor());
    }

    CommandNode<?> child = findChild(cmdReader.readUnquoted()).orElse(null);
    if (child == null) {
      throw CommandException.unkCommand();
    }

    if (!child.getSourceType().isInstance(context.getSource())) {
      throw CommandException.illegalSender();
    }

    return ((CommandNode<S>) child).execute(context);
  }

  public boolean hasRedirect() {
    return redirectNode != null;
  }

  // HELPER METHODS

  private static <T> T execute(CommandContext<?> context, Function<CommandContext<?>, ?> executor) {
    if (executor == null) {
      throw CommandException.notExecutable();
    }

    try {
      return (T) executor.apply(context);
    } catch (Exception e) {
      throw CommandException.execution(e);
    }
  }

}
