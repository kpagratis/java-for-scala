package io.pagratis.scalaforjava;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public sealed interface Option<T> permits Some, None {
  static <T> Option<T> none() {
    return new None<>();
  }

  static <T> Option<T> ofNullable(T value) {
    return value == null ? None.of() : Some.of(value);
  }

  static <T> Option<T> of(T value) {
    Objects.requireNonNull(value);
    return Some.of(value);
  }

  static <T> Option<T> fromJava(Optional<T> javaOptional) {
    return javaOptional.map(Option::of).orElse(Option.none());
  }

  Optional<T> toJava();

  Try<T> toTry();

  void ifPresent(Consumer<? super T> action);

  void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction);

  Option<T> filter(Predicate<? super T> predicate);

  <U> Option<U> map(Function<? super T, ? extends U> mapper);

  <U> Option<U> flatMap(Function<? super T, ? extends Option<? extends U>> mapper);

  Option<T> or(Supplier<? extends Option<? extends T>> supplier);

  Stream<T> stream();

  T orElse(T other);

  T orElseGet(Supplier<? extends T> supplier);

  T orElseThrow();

  <X extends Exception> T orElseThrow(Supplier<X> f) throws X;
  <X extends Exception> T orElseThrow(Function<String, X> f, String message) throws X;
}
