package com.github.empee.commands.arguments;

import com.github.empee.commands.arguments.numbers.DoubleParser;
import com.github.empee.commands.arguments.numbers.IntegerParser;

import java.util.UUID;

public class ArgumentsBuilder {

  public Argument<String> stringType(String id) {
    return Argument.arg(id, new StringParser());
  }

  public Argument<String> greedyStringType(String id) {
    return Argument.arg(id, new StringParser().greedy());
  }

  public Argument<String> quotableStringType(String id) {
    return Argument.arg(id, new StringParser().quotable());
  }

  public <E extends Enum<E>> Argument<E> enumType(String id, Class<E> enumClazz) {
    return Argument.arg(id, new EnumParser<>(enumClazz));
  }

  public Argument<UUID> uuidType(String id) {
    return Argument.arg(id, new UuidParser());
  }

  public Argument<Double> doubleType(String id) {
    return Argument.arg(id, new DoubleParser());
  }

  public Argument<Double> minDoubleType(String id, Double min) {
    return Argument.arg(id, new DoubleParser().withMin(min));
  }

  public Argument<Double> maxDoubleType(String id, Double max) {
    return Argument.arg(id, new DoubleParser().withMax(max));
  }

  public Argument<Double> rangeDoubleType(String id, Double min, Double max) {
    return Argument.arg(id, new DoubleParser().withMin(min).withMax(max));
  }

  public Argument<Integer> minIntegerType(String id, Integer min) {
    return Argument.arg(id, new IntegerParser().withMin(min));
  }

  public Argument<Integer> maxIntegerType(String id, Integer max) {
    return Argument.arg(id, new IntegerParser().withMax(max));
  }

  public Argument<Integer> rangeIntegerType(String id, Integer min, Integer max) {
    return Argument.arg(id, new IntegerParser().withMin(min).withMax(max));
  }

}
