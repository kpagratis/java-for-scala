package io.pagratis.scalaforjava;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record None<T>() implements Option<T> {
  public static <T> Option<T> of() {
    return new None<>();
  }

  @Override
  public Optional<T> toJava() {
    return Optional.empty();
  }

  @Override
  public Try<T> toTry() {
    return Throw.of(new NoSuchElementException("No value present"));
  }

  @Override
  public void ifPresent(Consumer<? super T> action) {

  }

  @Override
  public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
    emptyAction.run();
  }

  @Override
  public Option<T> filter(Predicate<? super T> predicate) {
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
    return (Option<U>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> Option<U> flatMap(Function<? super T, ? extends Option<? extends U>> mapper) {
    return (Option<U>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Option<T> or(Supplier<? extends Option<? extends T>> supplier) {
    return (Option<T>) supplier.get();
  }

  @Override
  public Stream<T> stream() {
    return Stream.empty();
  }

  @Override
  public T orElse(T other) {
    return other;
  }

  @Override
  public T orElseGet(Supplier<? extends T> supplier) {
    return supplier.get();
  }

  @Override
  public T orElseThrow() {
    throw new NoSuchElementException("No value present");
  }

  @Override
  public <X extends Exception> T orElseThrow(Supplier<X> f) throws X {
    throw f.get();
  }

  @Override
  public <X extends Exception> T orElseThrow(Function<String, X> f, String message) throws X {
    throw f.apply(message);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof None<?>;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return "None";
  }
}
