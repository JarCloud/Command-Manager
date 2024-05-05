package net.jarcloud.server.commands.builder;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Value
public class SuggestionContext {

  int start;
  int length;

  @NotNull List<CommandSuggestion> suggestions;

}
