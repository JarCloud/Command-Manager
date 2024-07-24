package com.github.empee.commands.suggestions;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Value
public class SuggestionContext {

  int start;
  int length;

  @NotNull List<CommandSuggestion> suggestions;

}
