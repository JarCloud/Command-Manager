package com.github.empee.commands.spigot;

import com.github.empee.commands.CommandManager;
import com.github.empee.commands.CommandNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BukkitInjector {

  private final Plugin plugin;
  private final CommandManager<CommandSender> commandManager;

  private final Constructor<PluginCommand> pluginCommandConstructor = getPluginCommandConstructor();
  private final CommandMap commandMap = getCommandMapField();

  @SneakyThrows
  private Constructor<PluginCommand> getPluginCommandConstructor() {
    Class<PluginCommand> clazz = PluginCommand.class;
    Constructor<PluginCommand> constructor = clazz.getDeclaredConstructor(String.class, Plugin.class);

    constructor.setAccessible(true);

    return constructor;
  }

  @SneakyThrows
  private CommandMap getCommandMapField() {
    Class<SimplePluginManager> clazz = SimplePluginManager.class;
    Field commandMapField = clazz.getDeclaredField("commandMap");
    commandMapField.setAccessible(true);

    return (CommandMap) commandMapField.get(Bukkit.getPluginManager());
  }

  @SneakyThrows
  public void register(CommandNode<?> command) {
    PluginCommand bukkitCommand = pluginCommandConstructor.newInstance(command.getLiteral(), plugin);

    List<String> aliases = command.getAliases().stream()
        .map(CommandNode::getLiteral)
        .collect(Collectors.toList());

    bukkitCommand.setAliases(aliases);
    bukkitCommand.setExecutor(new BukkitCommandExecutor(commandManager));

    commandMap.register(command.getLiteral(), bukkitCommand);

    if (CommodoreProvider.isSupported()) {
      Commodore commodore = CommodoreProvider.getCommodore(plugin);
      commodore.register(bukkitCommand, CommodoreAdapter.adaptNode(command));
      bukkitCommand.setTabCompleter(new BukkitCommandTabCompleter(commandManager));
    }
  }

}
