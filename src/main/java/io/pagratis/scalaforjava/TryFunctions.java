package io.pagratis.scalaforjava;

public interface TryFunctions {
  @FunctionalInterface
  interface ThrowingSupplier<T> {
    T get() throws Exception;
  }

  @FunctionalInterface
  interface ThrowingRunnable {
    void run() throws Exception;
  }

  @FunctionalInterface
  interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
  }

  @FunctionalInterface
  interface Throwing2Function<T1, T2, R> {
    R apply(T1 t1, T2 t2) throws Exception;
  }
}
