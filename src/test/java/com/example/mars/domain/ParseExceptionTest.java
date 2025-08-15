package com.example.mars.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ParseExceptionTest {

  @Test
  void constructor_withMessage_setsMessage() {
    String message = "Test error message";
    ParseException exception = new ParseException(message);

    assertThat(exception.getMessage()).isEqualTo(message);
    assertThat(exception.getCause()).isNull();
  }

  @Test
  void constructor_withMessageAndCause_setsBoth() {
    String message = "Test error message";
    Throwable cause = new NumberFormatException("Invalid number");
    ParseException exception = new ParseException(message, cause);

    assertThat(exception.getMessage()).isEqualTo(message);
    assertThat(exception.getCause()).isEqualTo(cause);
  }

  @Test
  void inheritance_extendsException() {
    ParseException exception = new ParseException("test");

    assertThat(exception).isInstanceOf(Exception.class);
    assertThat(exception).isInstanceOf(Throwable.class);
  }

  @Test
  void constructor_withNullMessage_allowsNull() {
    ParseException exception = new ParseException(null);

    assertThat(exception.getMessage()).isNull();
  }

  @Test
  void constructor_withNullCause_allowsNull() {
    String message = "Test message";
    ParseException exception = new ParseException(message, null);

    assertThat(exception.getMessage()).isEqualTo(message);
    assertThat(exception.getCause()).isNull();
  }

  @Test
  void toString_containsClassName() {
    ParseException exception = new ParseException("test message");
    String toString = exception.toString();

    assertThat(toString).contains("ParseException");
    assertThat(toString).contains("test message");
  }
}
