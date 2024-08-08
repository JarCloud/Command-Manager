package com.github.empee.commands.exceptions;

import com.github.empee.commands.arguments.ArgumentParser;
import lombok.Getter;

@Getter
public class ArgumentException extends CommandException {

  private String input;
  private String causeID;
  private ArgumentParser<?> parser;

  public static ArgumentException notFound(ArgumentParser<?> parser) {
    var exception = new ArgumentException();

    exception.type = Type.ARG_NOT_FOUND;
    exception.parser = parser;

    return exception;
  }

  public static ArgumentException parsing(ArgumentParser<?> parser, String input, Throwable cause) {
    var exception = new ArgumentException();

    exception.type = Type.ARG_PARSING_ERROR;
    exception.parser = parser;
    exception.input = input;
    exception.cause = cause;

    return exception;
  }

  public static ArgumentException parsing(ArgumentParser<?> parser, String input, String causeID) {
    var exception = new ArgumentException();

    exception.type = Type.ARG_PARSING_ERROR;
    exception.parser = parser;
    exception.input = input;
    exception.causeID = causeID;

    return exception;
  }

}
