package com.github.empee.commands.minestom.builder.arguments;

import com.github.empee.commands.CommandContext;
import com.github.empee.commands.arguments.Argument;
import com.github.empee.commands.arguments.properties.StringProperties;
import com.github.empee.commands.exceptions.ArgumentException;
import com.github.empee.commands.suggestions.CommandSuggestion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class PlayerArgument implements Argument<Player> {

  public static final String ERROR_NOT_ONLINE = "player_not_online";

  private final ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
  private final String id;


  private Function<CommandContext<?>, Player> defaultValue;
  private Function<CommandContext<?>, ?> executor;
  private Function<CommandContext<?>, List<CommandSuggestion>> suggestions = sender -> {
    return connectionManager.getOnlinePlayers().stream()
        .map(p -> CommandSuggestion.of(p.getUsername(), null))
        .toList();
  };

  public String getParser() {
    return "brigadier:string";
  }

  @Override
  public StringProperties getProperties() {
    return StringProperties.word();
  }

  public @NotNull Player parse(CommandContext<?> context, String input) {
    var result = connectionManager.getOnlinePlayerByUsername(input);
    if (result == null) {
      throw ArgumentException.parsing(this, input, ERROR_NOT_ONLINE);
    }

    return result;
  }

  public PlayerArgument withExecutor(Function<CommandContext<?>, ?> executor) {
    this.executor = executor;
    return this;
  }
  public PlayerArgument withExecutor(Consumer<CommandContext<?>> executor) {
    return withExecutor((ctx) -> {
      executor.accept(ctx);
      return null;
    });
  }

  public PlayerArgument withDefaultValue(Function<CommandContext<?>, Player> value) {
    defaultValue = value;
    return this;
  }
}
