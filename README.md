# Command Creation
```java
  public CommandNode<CommandSender> get() {
    return CommandNode.of("mysticalbarriers", CommandSender.class)
        .withPermission(s -> s.hasPermission(Permissions.ADMIN))
        .withAliases("mb")
        .withChild(wand())
        .withChild(create())
        .withChild(modify());
  }

  public CommandNode<Player> wand() {
    return CommandNode.of("wand", Player.class)
        .withExecutor(c -> {
          Player player = c.getSource();

          player.getInventory().addItem(selectionWand.get());
          Messenger.log(player, "&aSelection wand given");
        });
  }

  public CommandNode<Player> modify() {
    return CommandNode.of("edit", Player.class)
        .withArgs(BarrierArgument.of("barrier"))
        .withExecutor(c -> {
          PluginGUI.get(BarrierEditGUI.class).open(c.getSource(), c.get("barrier"));
        });
  }

  public CommandNode<Player> create() {
    return CommandNode.of("create", Player.class)
        .withArgs(StringArgument.of("id"))
        .withExecutor(c -> {
          Player player = c.getSource();
          String id = c.get("id");

          if (barriersService.findById(id).isPresent()) {
            Messenger.log(player, "&cA barrier with that id already exists");
            return;
          }

          var selection = selectionWand.getSelection(player.getUniqueId()).orElse(null);
          if (selection == null || !selection.isValid()) {
            Messenger.log(player, "&cYou haven't made a valid selection");
            return;
          }

          barriersService.createBarrier(id, selection);
          selectionWand.invalidate(player.getUniqueId());

          Messenger.log(player, "&aBarrier created");
        });
  }
```

# Custom argument creation
```java
@Getter
@RequiredArgsConstructor(staticName = "of")
public class BarrierArgument implements Argument<Barrier> {

  public static final String ERROR_NOT_FOUND = "barrier_not_found";

  private final String id;
  private final BarriersService barriersService = MysticalBarriers.getInstance(BarriersService.class);

  private Function<CommandContext<?>, ?> executor;
  private final Function<CommandContext<?>, List<CommandSuggestion>> suggestions = suggestionProvider();

  private @NotNull Function<CommandContext<?>, List<CommandSuggestion>> suggestionProvider() {
    return ctx -> barriersService.findAll().stream()
        .map(b -> CommandSuggestion.of(b.getId(), null))
        .collect(Collectors.toList());
  }

  public String getParser() {
    return "brigadier:string";
  }

  @Override
  public @NotNull Barrier parse(CommandContext<?> context, String input) {
    return barriersService.findById(input).orElseThrow(
        () -> ArgumentException.parsing(this, input, ERROR_NOT_FOUND)
    );
  }

  public BarrierArgument withExecutor(Function<CommandContext<?>, ?> executor) {
    this.executor = executor;
    return this;
  }

  public BarrierArgument withExecutor(Consumer<CommandContext<?>> executor) {
    return withExecutor((ctx) -> {
      executor.accept(ctx);
      return null;
    });
  }

}
```
