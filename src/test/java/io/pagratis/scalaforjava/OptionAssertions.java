package io.pagratis.scalaforjava;

import static org.junit.jupiter.api.Assertions.fail;

public class OptionAssertions {

  private OptionAssertions() {

  }

  public static <T> T assertSome(Option<T> o) {
    return switch (o) {
      case Some<T> s -> s.value();
      case default -> fail("was a None");
    };
  }

  public static <T> void assertNone(Option<T> o) {
    if (o instanceof Some<T> s) {
      fail("was a Some(%s)".formatted(s.value()));
    }
  }
}
