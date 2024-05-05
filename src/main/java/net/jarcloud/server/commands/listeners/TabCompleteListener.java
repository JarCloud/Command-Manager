package net.jarcloud.server.commands.listeners;

import net.jarcloud.server.commands.CommandManager;
import net.jarcloud.server.commands.builder.CommandSuggestion;
import net.jarcloud.server.commands.builder.SuggestionContext;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.entity.Player;
import net.minestom.server.message.Messenger;
import net.minestom.server.network.packet.client.play.ClientCommandChatPacket;
import net.minestom.server.network.packet.client.play.ClientTabCompletePacket;
import net.minestom.server.network.packet.server.play.TabCompletePacket;

import java.util.List;

public class TabCompleteListener {

  private static final CommandManager COMMAND_MANAGER = CommandManager.INSTANCE;

  public static void onTabComplete(ClientTabCompletePacket packet, Player player) {
    String text = packet.text();

    if (text.startsWith("/")) {
      text = text.substring(1);
    }

    SuggestionContext context = COMMAND_MANAGER.getSuggestions(player, text);
    if (context == null || context.getSuggestions().isEmpty()) {
      return;
    }

    var entries = context.getSuggestions().stream()
        .map(s -> new TabCompletePacket.Match(s.getValue(), s.getTooltip()))
        .toList();

    player.sendPacket(new TabCompletePacket(packet.transactionId(), context.getStart(), context.getLength(), entries));
  }

}
