package net.jarcloud.server.commands.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class CommandReader {

  private static final char ESCAPING = '\\';
  private static final char SEPARATOR = ' ';
  private static final char[] QUOTATIONS = {'\"', '\''};

  @Getter
  private final String text;

  @Getter @Setter
  private int cursor;
  private Integer oldCursor;

  public void back() {
    cursor = oldCursor;
    oldCursor = null;
  }

  public boolean hasReachedEnd() {
    return cursor >= text.length();
  }

  public boolean canRead() {
    return cursor < text.length();
  }

  private char peek(int offset) {
    return text.charAt(offset);
  }

  public boolean isQuotation(char input) {
    for (char quotation : QUOTATIONS) {
      if (quotation == input) {
        return true;
      }
    }

    return false;
  }

  private String read(char terminator, @Nullable Character escaping) {
    StringBuilder result = new StringBuilder();

    while (cursor < text.length() && peek(cursor) != terminator) {
      if (escaping != null && cursor+1 < text.length() && peek(cursor) == escaping && peek(cursor+1) == terminator) {
        result.append(peek(cursor+1));
        cursor += 2;
      } else {
        result.append(peek(cursor));
        cursor += 1;
      }
    }

    return result.toString();
  }

  public String read() {
    if (cursor+1 >= text.length()) {
      skipSeparator();
      return "";
    }

    char nextChar = peek(cursor+1); //Account for separator
    if (isQuotation(nextChar)) {
      return readQuoted(nextChar);
    } else {
      return readUnquoted();
    }
  }

  private void skipSeparator() {
    if (cursor == 0) {
      return; //Don't skip cause there isn't any ;)
    }

    oldCursor = cursor;
    cursor += 1;
  }

  public String readQuoted(char quotationChar) {
    skipSeparator();

    cursor += 1; //Skip quotation
    String result = read(quotationChar, ESCAPING);
    cursor += 1; //Skip closing quotation

    return result;
  }

  public String readUnquoted() {
    skipSeparator();

    return read(SEPARATOR, null);
  }

  public String readRemaining() {
    String result = text.substring(cursor);

    oldCursor = cursor;
    cursor = text.length();

    return result;
  }

}
