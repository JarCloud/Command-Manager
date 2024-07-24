package com.github.empee.commands.spigot;

import com.github.empee.commands.CommandManager;
import com.github.empee.commands.suggestions.CommandSuggestion;
import com.github.empee.commands.suggestions.SuggestionContext;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BukkitCommandTabCompleter implements TabCompleter {

  private final CommandManager<CommandSender> commandManager;

  @Nullable
  public List<String> onTabComplete(@NotNull CommandSender source, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    StringBuilder rawCmd = new StringBuilder(label);
    for (String arg : args) {
      rawCmd.append(" ").append(arg);
    }

    SuggestionContext context = commandManager.getSuggestions(source, rawCmd.toString());

    return context.getSuggestions().stream()
        .map(CommandSuggestion::getValue)
        .collect(Collectors.toList());
  }

}
