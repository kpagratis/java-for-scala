package io.pagratis.scalaforjava;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public sealed interface Try<T> permits Return, Throw {
  static <T> Try<T> of(TryFunctions.ThrowingSupplier<T> f) {
    try {
      Objects.requireNonNull(f);
      return Return.of(f.get());
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      return Throw.of(e);
    }
  }

  static Try<Void> of(TryFunctions.ThrowingRunnable f) {
    try {
      Objects.requireNonNull(f);
      f.run();
      return Return.ofVoid();
    } catch (Exception e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      return Throw.of(e);
    }
  }

  static <T> Try<T> requirePresent(Option<T> maybeValue) {
    return maybeValue
        .map(Return::of)
        .orElseGet(() -> Throw.of(new NoSuchElementException()));
  }

  @SuppressWarnings("unchecked")
  static <T> Try<Stream<T>> collect(Stream<Try<T>> stream) {
    final var collected = stream.collect(Collectors.groupingBy(tTry -> tTry.getClass()));
    final var returnResults = collected.getOrDefault(Return.class, List.of());
    final var throwResults = collected.getOrDefault(Throw.class, List.of());

    if (throwResults.isEmpty()) {
      final Stream<T> values = (Stream<T>) returnResults.stream().map(Return.class::cast).map(Return::value);
      return Return.of(values);
    } else {
      final List<Exception> values = throwResults.stream().map(Throw.class::cast).map(Throw::exception).toList();
      final var exception = values.get(0);
      if (values.size() == 1) {
        return Throw.of(exception);
      }
      values.subList(1, values.size()).forEach(exception::addSuppressed);
      return Throw.of(exception);
    }
  }

  static <A, B, R> Try<R> join(Try<A> tryA, Try<B> tryB, TryFunctions.Throwing2Function<A, B, R> f) {
    final var throwTrys = Stream
        .of(tryA, tryB)
        .filter(Throw.class::isInstance)
        .map(Throw.class::cast)
        .toList();
    return switch (throwTrys.size()) {
      case 0 -> tryA.flatMap(a -> tryB.map(b -> f.apply(a, b)));
      case default -> collapseThrows(throwTrys);
    };
  }

  static <A, B, C, R> Try<R> join(Try<A> tryA, Try<B> tryB, Try<C> tryC, TryFunctions.Throwing3Function<A, B, C, R> f) {
    final var throwTrys = Stream
        .of(tryA, tryB, tryC)
        .filter(Throw.class::isInstance)
        .map(Throw.class::cast)
        .toList();
    return switch (throwTrys.size()) {
      case 0 -> tryA.flatMap(a -> tryB.flatMap(b -> tryC.map(c -> f.apply(a, b, c))));
      case default -> collapseThrows(throwTrys);
    };
  }

  static private <R> Try<R> collapseThrows(List<Throw> throwTrys) {
    return switch (throwTrys.size()) {
      case 1 -> Throw.of(throwTrys.get(0).exception());
      default -> {
        final var exception = throwTrys.get(0).exception();
        throwTrys.subList(1, throwTrys.size()).stream().map(Throw::exception).forEach(exception::addSuppressed);
        yield Throw.of(exception);
      }
    };
  }

  Try<Option<T>> filter(Predicate<T> f);

  Try<Option<T>> filterNot(Predicate<T> f);

  static <T> Try<T> requireNonNull(T value) {
    return Try.of(() -> Objects.requireNonNull(value));
  }

  <R> Try<R> map(TryFunctions.ThrowingFunction<T, R> f);

  <R> Try<R> flatMap(TryFunctions.ThrowingFunction<T, Try<R>> f);

  Try<T> recover(TryFunctions.ThrowingFunction<Exception, ? extends Try<T>> f);

  T orElseThrow() throws Exception;

  <E extends Exception> T orElseThrow(Function<Exception, E> f) throws E;

  <E extends Exception> T orElseThrow(BiFunction<String, Exception, E> f, String message) throws E;
}
