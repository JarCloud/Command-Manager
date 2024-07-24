package com.github.empee.commands.minestom.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.github.empee.commands.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.message.Messenger;
import net.minestom.server.network.packet.client.play.ClientCommandChatPacket;

@Slf4j
@RequiredArgsConstructor
public class ClientCommandListener {

  private final CommandManager<CommandSender> commandManager;

  public void onClientCommand(ClientCommandChatPacket packet, Player player) {
    String command = packet.message();

    if (!Messenger.canReceiveCommand(player)) {
      Messenger.sendRejectionMessage(player);
      return;
    }

    PlayerCommandEvent commandEvent = new PlayerCommandEvent(player, command);
    EventDispatcher.call(commandEvent);
    if (commandEvent.isCancelled()) {
      return;
    }

    command = commandEvent.getCommand();
    commandManager.execute(player, command);
  }

}
