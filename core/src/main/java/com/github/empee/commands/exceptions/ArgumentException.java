package com.github.empee.commands.exceptions;

import com.github.empee.commands.arguments.Argument;
import lombok.Getter;

@Getter
public class ArgumentException extends CommandException {

  private String input;
  private String causeID;
  private Argument<?> argument;

  public static ArgumentException notFound(Argument<?> argument) {
    var exception = new ArgumentException();

    exception.type = Type.ARG_NOT_FOUND;
    exception.argument = argument;

    return exception;
  }

  public static ArgumentException parsing(Argument<?> argument, String input, Throwable cause) {
    var exception = new ArgumentException();

    exception.type = Type.ARG_PARSING_ERROR;
    exception.argument = argument;
    exception.input = input;
    exception.cause = cause;

    return exception;
  }

  public static ArgumentException parsing(Argument<?> argument, String input, String causeID) {
    var exception = new ArgumentException();

    exception.type = Type.ARG_PARSING_ERROR;
    exception.argument = argument;
    exception.input = input;
    exception.causeID = causeID;

    return exception;
  }

}
