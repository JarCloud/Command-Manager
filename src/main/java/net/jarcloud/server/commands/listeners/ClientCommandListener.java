package net.jarcloud.server.commands.listeners;

import lombok.extern.slf4j.Slf4j;
import net.jarcloud.server.commands.CommandManager;
import net.minestom.server.entity.Player;
import net.minestom.server.message.Messenger;
import net.minestom.server.network.packet.client.play.ClientCommandChatPacket;

@Slf4j
public class ClientCommandListener {

  private static final CommandManager COMMAND_MANAGER = CommandManager.INSTANCE;

  public static void onClientCommand(ClientCommandChatPacket packet, Player player) {
    String command = packet.message();

    if (Messenger.canReceiveCommand(player)) {
      COMMAND_MANAGER.execute(player, command);
    } else {
      Messenger.sendRejectionMessage(player);
    }
  }

}
