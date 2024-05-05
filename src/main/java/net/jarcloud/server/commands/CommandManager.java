package net.jarcloud.server.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.jarcloud.server.commands.builder.CommandContext;
import net.jarcloud.server.commands.builder.CommandNode;
import net.jarcloud.server.commands.builder.CommandReader;
import net.jarcloud.server.commands.builder.CommandSuggestion;
import net.jarcloud.server.commands.builder.SuggestionContext;
import net.jarcloud.server.commands.utils.DeclareCommandsPacketUtils;
import net.jarcloud.server.commands.builder.exceptions.CommandException;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;

import java.util.List;
import java.util.function.BiConsumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandManager {

  public static final CommandManager INSTANCE = new CommandManager();

  private final CommandNode root = CommandNode.of("");

  @Setter
  private BiConsumer<CommandContext, CommandException> exceptionHandler = (ctx, e) -> {
    ctx.sender().sendMessage("Error while executing command, type " + e.getType());
  };

  public void register(CommandNode command) {
    root.withChild(command);
  }

  public <T> T execute(CommandSender sender, String input) {
    input = input.trim();

    if (sender instanceof Player) {
      PlayerCommandEvent playerCommandEvent = new PlayerCommandEvent((Player) sender, input);
      EventDispatcher.call(playerCommandEvent);
      if (playerCommandEvent.isCancelled()) {
        return null;
      }

      input = playerCommandEvent.getCommand();
    }

    CommandContext context = CommandContext.of(new CommandReader(input), sender);

    try {
      return root.execute(context);
    } catch (CommandException e) {
      exceptionHandler.accept(context, e);
    }

    return null;
  }

  public SuggestionContext getSuggestions(CommandSender sender, String input) {
    return root.getSuggestions(CommandContext.of(new CommandReader(input), sender));
  }

  public DeclareCommandsPacket buildDeclareCommandsPacket(Player player) {
    return DeclareCommandsPacketUtils.build(root, player);
  }

}
