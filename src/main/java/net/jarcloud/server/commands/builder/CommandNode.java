package net.jarcloud.server.commands.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jarcloud.server.commands.builder.arguments.Argument;
import net.jarcloud.server.commands.builder.exceptions.CommandException;
import net.minestom.server.command.CommandSender;
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
public class CommandNode {

  private final String literal;

  private final List<CommandNode> children = new ArrayList<>();
  private final List<Argument<?>> arguments = new ArrayList<>();
  private final List<CommandNode> aliases = new ArrayList<>();

  private CommandNode redirectNode;
  private Function<CommandContext, ?> defaultExecutor;
  private Function<CommandSender, ? extends CommandException> accessTester;

  public CommandNode withChild(CommandNode child) {
    if(findChild(child.literal).isPresent()) {
      throw new IllegalArgumentException("Node '" + child.literal + "' already registered!");
    }

    children.add(child);
    children.addAll(child.aliases);

    return this;
  }

  public CommandNode withArgs(Argument<?>... args) {
    arguments.addAll(Arrays.asList(args));
    return this;
  }

  public CommandNode withDefaultExecutor(Function<CommandContext, ?> executable) {
    defaultExecutor = executable;

    return this;
  }

  public CommandNode withDefaultExecutor(Consumer<CommandContext> executable) {
    return withDefaultExecutor(ctx -> {
      executable.accept(ctx);
      return null;
    });
  }

  public CommandNode withExecutor(Function<CommandContext, ?> executable) {
    if (arguments.isEmpty()) {
      defaultExecutor = executable;
    } else {
      arguments.getLast().withExecutor(executable);
    }

    return this;
  }

  public CommandNode withExecutor(Consumer<CommandContext> executable) {
    return withExecutor(ctx -> {
      executable.accept(ctx);
      return null;
    });
  }

  public CommandNode withAccessTester(Function<CommandSender, ? extends CommandException> accessTester) {
    this.accessTester = accessTester;
    return this;
  }

  public CommandNode withPermission(Predicate<CommandSender> accessTester) {
    this.accessTester = sender -> accessTester.test(sender) ? null : CommandException.missingPermission();
    return this;
  }

  public CommandNode withAliases(String... aliases) {
    for (String alias : aliases) {
      var node = CommandNode.of(alias);
      node.redirectNode = this;
      this.aliases.add(node);
    }

    return this;
  }

  public Optional<CommandNode> findChild(String literal) {
    return children.stream()
        .filter(c -> c.getLiteral().equalsIgnoreCase(literal))
        .findAny();
  }

  @Nullable
  public SuggestionContext getSuggestions(CommandContext context) {
    if (redirectNode != null) {
      return redirectNode.getSuggestions(context);
    }

    if (accessTester != null) {
      var result = accessTester.apply(context.sender());
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

    CommandNode child = findChild(reader.readUnquoted()).orElse(null);
    if (child == null) {
      return null;
    }

    return child.getSuggestions(context);
  }

  public <T> T execute(CommandContext context) {
    if (redirectNode != null) {
      return redirectNode.execute(context);
    }

    if (accessTester != null) {
      var result = accessTester.apply(context.sender());
      if (result != null) {
        throw result;
      }
    }

    CommandReader cmdReader = context.getReader();
    if (cmdReader.hasReachedEnd()) {
      return execute(context, defaultExecutor);
    }

    for (Argument<?> argument : arguments) {
      context.set(argument.getId(), argument.parse(context));
    }

    if (cmdReader.hasReachedEnd()) {
      return execute(context, arguments.getLast().getExecutor());
    }

    CommandNode child = findChild(cmdReader.readUnquoted()).orElse(null);
    if (child == null) {
      throw CommandException.unkCommand();
    }

    return child.execute(context);
  }

  public boolean hasRedirect() {
    return redirectNode != null;
  }

  // HELPER METHODS

  private static <T> T execute(CommandContext context, Function<CommandContext, ?> executor) {
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
