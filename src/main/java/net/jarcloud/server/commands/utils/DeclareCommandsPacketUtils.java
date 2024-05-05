package net.jarcloud.server.commands.utils;

import lombok.experimental.UtilityClass;
import net.jarcloud.server.commands.builder.CommandNode;
import net.jarcloud.server.commands.builder.arguments.Argument;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket.Node;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket.NodeType;

import java.util.ArrayList;
import java.util.List;

import static net.minestom.server.network.packet.server.play.DeclareCommandsPacket.getFlag;

@UtilityClass
public class DeclareCommandsPacketUtils {

  public DeclareCommandsPacket build(CommandNode command, Player player) {
    return new DeclareCommandsPacket(buildNodeGraphOf(command, player), 0);
  }

  private Node buildCommandNode(CommandNode command) {
    Node node = new Node();
    node.name = command.getLiteral();

    NodeType type = command.getLiteral().isEmpty() ? NodeType.ROOT : NodeType.LITERAL;
    boolean isExecutable = command.getDefaultExecutor() != null;

    node.flags = getFlag(type, isExecutable, command.hasRedirect(), false);

    return node;
  }

  private Node buildArgumentNode(Argument<?> argument) {
    var node = new Node();

    node.name = argument.getId();
    node.parser = argument.getParser();

    boolean hasProperties = argument.getProperties() != null;
    boolean hasSuggestions = argument.getSuggestionType() != null;
    boolean isExecutable = argument.getExecutor() != null;

    if (hasProperties) {
      node.properties = argument.getProperties().get();
    }

    if (hasSuggestions) {
      node.suggestionsType = argument.getSuggestionType().getIdentifier();
    }

    node.flags = getFlag(NodeType.ARGUMENT, isExecutable, false, hasSuggestions);

    return node;
  }

  private List<Node> buildNodeGraphOf(CommandNode command, Player player) {
    List<Node> nodes = new ArrayList<>();

    Node node = buildCommandNode(command);
    Node parent = node;

    //Index 0, this node
    nodes.add(node);
    //Then all the aliases of this node
    for (CommandNode alias : command.getAliases()) {
      nodes.addAll(buildNodeGraphOf(alias, player));
    }
    //Then all the arguments of this node (Also register them, by populating the children section of the main node)
    for (Argument<?> argument : command.getArguments()) {
      if (argument.getDefaultValue() != null) {
        setExecutable(parent);
      }

      parent.children = new int[] { nodes.size() };
      parent = buildArgumentNode(argument);
      nodes.add(parent);
    }
    //Then all the children nodes (Also register them, by populating the children section of the main node)
    var childrenIndexes = new ArrayList<Integer>();
    for (CommandNode child : command.getChildren()) {
      //This code merges all the children nodes with all the nodes provided previously
      if (child.hasRedirect()) {
        continue; //Ignore redirects, because they are registered when their destination node is registered
      }

      if (child.getAccessTester() != null && child.getAccessTester().apply(player) != null) {
        continue; //Ignore this node, player doesn't have access to it
      }

      var childNodes = buildNodeGraphOf(child, player);
      //Update the references of each node (Needed because we will merge the lists)
      applyOffsetToNodeReferences(childNodes, nodes.size());
      //Register the children indexes
      childrenIndexes.add(nodes.size());
      for (int i = 1; i <= child.getAliases().size(); i++) {
        childrenIndexes.add(nodes.size() + i);
      }

      nodes.addAll(childNodes);
    }

    parent.children = childrenIndexes.stream()
        .mapToInt(Integer::intValue)
        .toArray();

    return nodes;
  }

  private static void applyOffsetToNodeReferences(List<Node> childNodes, int offset) {
    for (Node childNode : childNodes) {
      if (hasRedirect(childNode)) {
        childNode.redirectedNode = childNode.redirectedNode + offset;
      }

      for (int i=0; i<childNode.children.length; i++) {
        childNode.children[i] = childNode.children[i] + offset;
      }
    }
  }

  private static boolean hasRedirect(Node node) {
    return (node.flags & 0x08) != 0;
  }

  private static void setExecutable(Node node) {
    node.flags = (byte) (node.flags | 4);
  }

}
