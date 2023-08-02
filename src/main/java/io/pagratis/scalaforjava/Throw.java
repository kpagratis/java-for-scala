package io.pagratis.scalaforjava;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public record Throw<T>(Exception exception) implements Try<T> {
  static <T> Try<T> of(Exception exception) {
    return new Throw<>(exception);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Try<Option<T>> filter(Predicate<T> f) {
    return (Try<Option<T>>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Try<Option<T>> filterNot(Predicate<T> f) {
    return (Try<Option<T>>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> Try<R> map(TryFunctions.ThrowingFunction<T, R> f) {
    return (Try<R>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> Try<R> flatMap(TryFunctions.ThrowingFunction<T, Try<R>> f) {
    return (Try<R>) this;
  }

  @Override
  public Try<T> recover(TryFunctions.ThrowingFunction<Exception, ? extends Try<T>> f) {
    try {
      return f.apply(exception);
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      return Throw.of(e);
    }
  }

  @Override
  public T orElseThrow() throws Exception {
    throw exception;
  }

  @Override
  public <E extends Exception> T orElseThrow(Function<Exception, E> f) throws E {
    throw f.apply(exception);
  }

  @Override
  public <E extends Exception> T orElseThrow(BiFunction<String, Exception, E> f, String message) throws E {
    throw f.apply(message, exception);
  }
}
