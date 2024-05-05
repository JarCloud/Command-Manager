package net.jarcloud.server.commands;

import net.jarcloud.server.commands.builder.CommandContext;
import net.jarcloud.server.commands.builder.CommandNode;
import net.jarcloud.server.commands.builder.exceptions.CommandException;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.entity.Player;

public abstract class JarCommand {

  public abstract CommandNode get();

  protected Player player(CommandContext context) {
    if (context.sender() instanceof Player) {
      return (Player) context.sender();
    }

    throw CommandException.illegalSender();
  }

  protected ConsoleSender console(CommandContext context) {
    if (context.sender() instanceof ConsoleSender) {
      return (ConsoleSender) context.sender();
    }

    throw CommandException.illegalSender();
  }

  protected ServerSender server(CommandContext context) {
    if (context.sender() instanceof ServerSender) {
      return (ServerSender) context.sender();
    }

    throw CommandException.illegalSender();
  }

}
