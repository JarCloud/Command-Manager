package com.github.empee.commands.suggestions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuggestionType {

  public static final SuggestionType ASK_SERVER = new SuggestionType("minecraft:ask_server");
  public static final SuggestionType ALL_RECIPES = new SuggestionType("minecraft:all_recipes");
  public static final SuggestionType AVAILABLE_SOUNDS = new SuggestionType("minecraft:available_sounds");
  public static final SuggestionType SUMMONABLE_ENTITIES = new SuggestionType("minecraft:summonable_entities");

  @Getter
  private final String identifier;

}
