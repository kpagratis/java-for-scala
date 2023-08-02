package io.pagratis.scalaforjava;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public record Return<T>(T value) implements Try<T> {
  static <T> Try<T> of(T value) {
    return new Return<>(value);
  }

  static Try<Void> ofVoid() {
    return Return.of(null);
  }

  @Override
  public Try<Option<T>> filter(Predicate<T> f) {
    return Return.of(Option
        .of(f.test(value))
        .filter(b -> b)
        .map(__ -> value));
  }

  @Override
  public Try<Option<T>> filterNot(Predicate<T> f) {
    return Return.of(Option
        .of(f.test(value))
        .filter(b -> !b)
        .map(__ -> value));
  }

  @Override
  public <R> Try<R> map(TryFunctions.ThrowingFunction<T, R> f) {
    Objects.requireNonNull(f);
    try {
      return Return.of(f.apply(value));
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      return Throw.of(e);
    }
  }

  @Override
  public <R> Try<R> flatMap(TryFunctions.ThrowingFunction<T, Try<R>> f) {
    Objects.requireNonNull(f);
    try {
      return f.apply(value);
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      return Throw.of(e);
    }
  }

  @Override
  public Try<T> recover(TryFunctions.ThrowingFunction<Exception, ? extends Try<T>> f) {
    return this;
  }

  @Override
  public T orElseThrow() {
    return value;
  }

  @Override
  public <E extends Exception> T orElseThrow(Function<Exception, E> f) throws E {
    return value;
  }

  @Override
  public <E extends Exception> T orElseThrow(BiFunction<String, Exception, E> f, String message) throws E {
    return value;
  }
}
