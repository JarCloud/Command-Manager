package com.github.empee.commands.spigot;

import com.github.empee.commands.CommandNode;
import com.github.empee.commands.arguments.Argument;
import com.github.empee.commands.arguments.numbers.DoubleArgument;
import com.github.empee.commands.arguments.numbers.IntArgument;
import com.github.empee.commands.arguments.properties.ArgumentProperties;
import com.github.empee.commands.arguments.properties.DoubleProperties;
import com.github.empee.commands.arguments.properties.IntegerProperties;
import com.github.empee.commands.arguments.properties.StringProperties;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CommodoreAdapter {

  public LiteralArgumentBuilder adaptNode(CommandNode<?> command) {
    LiteralArgumentBuilder<?> root = LiteralArgumentBuilder.literal(command.getLiteral());

    if (command.getArguments().isEmpty()) {
      appendChildren(command, root);
    } else {
      appendArgumentsAndChildren(command, root);
    }

    return root;
  }

  private static void appendArgumentsAndChildren(CommandNode<?> command, LiteralArgumentBuilder<?> root) {
    ArgumentBuilder arg = null;
    var args = command.getArguments();
    for (int i=args.size() - 1; i >= 0; i--) {
      var brigadierArg = RequiredArgumentBuilder.argument(args.get(i).getId(), adaptType(args.get(i)));
      if (arg == null) {
        appendChildren(command, brigadierArg);
      } else {
        brigadierArg.then(arg.build());
      }

      arg = brigadierArg;
    }

    root.then(arg.build());
  }

  private static void appendChildren(CommandNode<?> command, ArgumentBuilder brigadierArg) {
    for (CommandNode<?> child : command.getChildren()) {
      var childBrigadier = adaptNode(child).build();
      brigadierArg.then(childBrigadier);

      for (CommandNode<?> alias : child.getAliases()) {
        var aliasBrigadier = adaptNode(alias);
        aliasBrigadier.redirect(childBrigadier);

        brigadierArg.then(aliasBrigadier.build());
      }
    }
  }

  private ArgumentType adaptType(Argument<?> argument) {
    ArgumentProperties properties = argument.getProperties();
    if (properties == null) {
      return StringArgumentType.word();
    }

    if (properties instanceof IntegerProperties) {
      return getIntegerArgumentType((IntegerProperties) properties);
    }

    if (properties instanceof DoubleProperties) {
      return getDoubleArgumentType((DoubleProperties) properties);
    }

    if (properties instanceof StringProperties) {
      return getStringArgumentType((StringProperties) properties);
    }

    throw new IllegalArgumentException("Property not adaptable to brigadier -> " + properties.getClass());
  }

  private static IntegerArgumentType getIntegerArgumentType(IntegerProperties properties) {
    if (properties.getMax() == null && properties.getMin() != null) {
      return IntegerArgumentType.integer(properties.getMin(), Integer.MAX_VALUE);
    }

    if (properties.getMax() != null && properties.getMin() == null) {
      return IntegerArgumentType.integer(Integer.MIN_VALUE, properties.getMax());
    }

    if (properties.getMax() != null && properties.getMin() != null) {
      return IntegerArgumentType.integer(properties.getMin(), properties.getMax());
    }

    return IntegerArgumentType.integer();
  }

  private static DoubleArgumentType getDoubleArgumentType(DoubleProperties properties) {
    if (properties.getMax() == null && properties.getMin() != null) {
      return DoubleArgumentType.doubleArg(properties.getMin(), Integer.MAX_VALUE);
    }

    if (properties.getMax() != null && properties.getMin() == null) {
      return DoubleArgumentType.doubleArg(Integer.MIN_VALUE, properties.getMax());
    }

    if (properties.getMax() != null && properties.getMin() != null) {
      return DoubleArgumentType.doubleArg(properties.getMin(), properties.getMax());
    }

    return DoubleArgumentType.doubleArg();
  }

  private static StringArgumentType getStringArgumentType(StringProperties properties) {
    if (properties.getType() == StringProperties.GREEDY) {
      return StringArgumentType.greedyString();
    }

    if (properties.getType() == StringProperties.QUOTABLE) {
      return StringArgumentType.string();
    }

    return StringArgumentType.word();
  }

}
