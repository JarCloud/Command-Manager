package com.github.empee.commands.minestom.listeners;

import com.github.empee.commands.CommandManager;
import com.github.empee.commands.suggestions.SuggestionContext;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.client.play.ClientTabCompletePacket;
import net.minestom.server.network.packet.server.play.TabCompletePacket;

@RequiredArgsConstructor
public class TabCompleteListener {

  private final CommandManager<CommandSender> commandManager;

  public void onTabComplete(ClientTabCompletePacket packet, Player player) {
    String text = packet.text();

    if (text.startsWith("/")) {
      text = text.substring(1);
    }

    SuggestionContext context = commandManager.getSuggestions(player, text);
    if (context == null || context.getSuggestions().isEmpty()) {
      return;
    }

    var entries = context.getSuggestions().stream()
        .map(s -> new TabCompletePacket.Match(s.getValue(), s.getTooltip().map(Component::text).orElse(null)))
        .toList();

    player.sendPacket(new TabCompletePacket(packet.transactionId(), context.getStart(), context.getLength(), entries));
  }

}
