package io.pagratis.scalaforjava;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record Some<T>(T value) implements Option<T> {

  public static <T> Option<T> of(T value) {
    return new Some<>(value);
  }

  @Override
  public Optional<T> toJava() {
    return Optional.of(value);
  }

  @Override
  public Try<T> toTry() {
    return Return.of(value);
  }

  @Override
  public void ifPresent(Consumer<? super T> f) {
    f.accept(value);
  }

  @Override
  public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
    action.accept(value);
  }

  @Override
  public Option<T> filter(Predicate<? super T> predicate) {
    return predicate.test(value) ? this : None.of();
  }

  @Override
  public Stream<T> stream() {
    return Stream.of(value);
  }

  @Override
  public T orElse(T other) {
    return value;
  }

  @Override
  public T orElseGet(Supplier<? extends T> supplier) {
    return value;
  }

  @Override
  public T orElseThrow() {
    return value;
  }

  @Override
  public <X extends Exception> T orElseThrow(Supplier<X> f) {
    return value;
  }

  @Override
  public <X extends Exception> T orElseThrow(Function<String, X> f, String message) throws X {
    return value;
  }

  @Override
  public Option<T> or(Supplier<? extends Option<? extends T>> supplier) {
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Option<U> flatMap(Function<? super T, ? extends Option<? extends U>> mapper) {
    return (Option<U>) mapper.apply(value);
  }

  @Override
  public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
    return Some.of(mapper.apply(value));
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Some<?> some && Objects.equals(value, some.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "Some(" + value + ")";
  }
}
