package com.github.empee.commands.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Getter
public class CommandException extends RuntimeException {

  protected Type type;
  protected String message;
  protected Throwable cause;

  public static CommandException execution(Throwable cause) {
    var exception = new CommandException();

    exception.type = Type.EXECUTION_ERROR;
    exception.cause = cause;

    return exception;
  }

  public static CommandException notExecutable() {
    var exception = new CommandException();

    exception.type = Type.NOT_EXECUTABLE;

    return exception;
  }

  public static CommandException syntax(String message) {
    var exception = new CommandException();

    exception.type = Type.SYNTAX_ERROR;
    exception.message = message;

    return exception;
  }

  public static CommandException unkCommand() {
    var exception = new CommandException();

    exception.type = Type.COMMAND_UNK;

    return exception;
  }

  public static CommandException illegalSender() {
    var exception = new CommandException();

    exception.type = Type.ILLEGAL_SENDER;

    return exception;
  }


  public static CommandException missingPermission() {
    var exception = new CommandException();

    exception.type = Type.MISSING_PERMISSION;

    return exception;
  }

  @Value
  @RequiredArgsConstructor(staticName = "of")
  public static class Type {
    public static final Type EXECUTION_ERROR = of("execution_error");
    public static final Type NOT_EXECUTABLE = of("not_executable");
    public static final Type SYNTAX_ERROR = of("syntax_error");
    public static final Type COMMAND_UNK = of("command_unk");
    public static final Type ARG_NOT_FOUND = of("arg_not_found");
    public static final Type ARG_PARSING_ERROR = of("arg_parsing_error");
    public static final Type MISSING_PERMISSION = of("missing_permission");
    public static final Type ILLEGAL_SENDER = of("illegal_sender");

    String id;

    public String toString() {
      return id;
    }
  }

}
