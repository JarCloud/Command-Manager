package com.github.empee.commands.minestom.builder.arguments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jarcloud.server.commands.CommandContext;
import net.jarcloud.server.commands.arguments.Argument;
import net.jarcloud.server.commands.arguments.properties.StringProperties;
import net.jarcloud.server.commands.exceptions.ArgumentException;
import net.jarcloud.server.commands.suggestions.CommandSuggestion;
import net.minestom.server.MinecraftServer;
import net.minestom.server.network.ConnectionManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class OfflinePlayerArgument implements Argument<UUID> {

  public static final String ERROR_SYNTAX = "uuid_syntax_error";

  private final ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
  private final String id;

  private Function<CommandContext<?>, UUID> defaultValue;
  private Function<CommandContext<?>, ?> executor;
  private Function<CommandContext<?>, List<CommandSuggestion>> suggestions = sender -> {
    return connectionManager.getOnlinePlayers().stream()
        .map(p -> CommandSuggestion.of(p.getUsername(), null))
        .toList();
  };;

  public String getParser() {
    return "brigadier:string";
  }

  @Override
  public StringProperties getProperties() {
    return StringProperties.word();
  }

  public @NotNull UUID parse(CommandContext<?> context, String input) {
    var result = connectionManager.getOnlinePlayerByUsername(input);
    if (result != null) {
      return result.getUuid();
    }

    try {
      return UUID.fromString(input);
    } catch (IllegalArgumentException e) {
      throw ArgumentException.parsing(this, input, ERROR_SYNTAX);
    }
  }

  public OfflinePlayerArgument withExecutor(Function<CommandContext<?>, ?> executor) {
    this.executor = executor;
    return this;
  }
  public OfflinePlayerArgument withExecutor(Consumer<CommandContext<?>> executor) {
    return withExecutor((ctx) -> {
      executor.accept(ctx);
      return null;
    });
  }

  public OfflinePlayerArgument withDefaultValue(Function<CommandContext<?>, UUID> value) {
    defaultValue = value;
    return this;
  }
}
