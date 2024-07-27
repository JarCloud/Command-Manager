package com.github.empee.commands.spigot;

import com.github.empee.commands.CommandManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class BukkitCommandExecutor implements CommandExecutor {

  private final CommandManager<CommandSender> commandManager;

  @Override
  public boolean onCommand(@NotNull CommandSender source, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    StringBuilder rawCmd = new StringBuilder(label);
    for (String arg : args) {
      rawCmd.append(" ").append(arg);
    }

    commandManager.execute(source, rawCmd.toString());

    return true;
  }

}
