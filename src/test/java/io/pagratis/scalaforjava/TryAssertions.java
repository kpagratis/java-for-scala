package io.pagratis.scalaforjava;

import static org.junit.jupiter.api.Assertions.fail;

public final class TryAssertions {
  private TryAssertions() {

  }

  public static <T> T assertReturn(Try<T> t) {
    return switch (t) {
      case Return<T> ret -> ret.value();
      case Throw<T> thrown -> fail("was throw: ", thrown.exception());
    };
  }

  public static <T> Exception assertThrow(Try<T> t) {
    return switch (t) {
      case Return<T> ret -> fail("was Return: %s".formatted(ret.value()));
      case Throw<T> thrown -> thrown.exception();
    };
  }
}
