package io.pagratis.scalaforjava;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Matches;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.pagratis.scalaforjava.OptionAssertions.assertNone;
import static io.pagratis.scalaforjava.OptionAssertions.assertSome;
import static io.pagratis.scalaforjava.TryAssertions.assertReturn;
import static io.pagratis.scalaforjava.TryAssertions.assertThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TryTest {

  @Test
  void of() {
    assertEquals("something", assertReturn(Try.of(() -> "something")));
    assertNull(assertReturn(Try.of(() -> {
    })));

    assertEquals(
        "oops",
        assertThrow(Try.of((TryFunctions.ThrowingSupplier<String>) () -> {
          throw new Exception("oops");
        })).getMessage());

    assertEquals(
        "oops",
        assertThrow(Try.of((TryFunctions.ThrowingRunnable) () -> {
          throw new Exception("oops");
        })).getMessage());

    assertEquals("value", assertReturn(Return.of("value")));
    assertEquals("oops", assertThrow(Throw.of(new Exception("oops"))).getMessage());
  }

  @Test
  void requirePresent() {
    final var nones = Stream.of(
        Try.requirePresent(Option.none()),
        Try.requirePresent(Option.ofNullable(null)),
        Try.requirePresent(Option.fromJava(Optional.empty())),
        Try.requirePresent(Option.fromJava(Optional.ofNullable(null)))
    );

    assertFalse(
        nones
            .map(TryAssertions::assertThrow)
            .anyMatch(Predicate.not(NoSuchElementException.class::isInstance))
    );

    final var somes = Stream.of(
        Try.requirePresent(Option.of("")),
        Try.requirePresent(Option.ofNullable("")),
        Try.requirePresent(Option.fromJava(Optional.of(""))),
        Try.requirePresent(Option.fromJava(Optional.ofNullable("")))
    );
    somes.forEach(TryAssertions::assertReturn);
  }

  @Test
  void filterFilterNot() {
    assertEquals(
        "value",
        assertSome(assertReturn(Try.of(() -> "value").filterNot(String::isEmpty)))
    );
    assertNone(assertReturn(Try.of(() -> "value").filter(String::isEmpty)));

    assertEquals(
        "value",
        assertSome(assertReturn(Return.of("value").filterNot(String::isEmpty)))
    );

    assertNone(assertReturn(Return.of("value").filter(String::isEmpty)));

    final var t = Throw.of(new Exception());
    assertSame(t, t.filter((__) -> false));
    assertSame(t, t.filter((__) -> true));
    assertSame(t, t.filterNot((__) -> false));
    assertSame(t, t.filterNot((__) -> true));
  }

  @Test
  void collect() {
    final var withThrows = Try.collect(Stream.of(
        Return.of("A"),
        Throw.of(new Exception("first")),
        Return.of("B"),
        Throw.of(new Exception("second"))
    ));
    final var exception1 = assertInstanceOf(Throw.class, withThrows).exception();
    assertEquals("first", exception1.getMessage());
    assertEquals(1, exception1.getSuppressed().length);
    assertEquals("second", exception1.getSuppressed()[0].getMessage());

    final var with3Throws = Try.collect(Stream.of(
        Return.of("A"),
        Throw.of(new Exception("first")),
        Return.of("B"),
        Throw.of(new Exception("second")),
        Throw.of(new Exception("third"))
    ));
    final var exception2 = assertInstanceOf(Throw.class, with3Throws).exception();
    assertEquals("first", exception2.getMessage());
    assertEquals(2, exception2.getSuppressed().length);
    assertEquals("second", exception2.getSuppressed()[0].getMessage());
    assertEquals("third", exception2.getSuppressed()[1].getMessage());

    final var list = assertReturn(
        Try.collect(
            Stream.of(
                Return.of(1),
                Return.of(2),
                Return.of(3)
            )
        )
    );
    assertEquals(List.of(1, 2, 3), list.toList());
  }

  @Test
  void requireNonNull() {
    assertEquals("value", assertReturn(Try.requireNonNull("value")));
    assertThrow(Try.requireNonNull(null));
  }

  @Test
  @SuppressWarnings("unchecked")
  void map() throws Exception {
    final var mockMap = mock(TryFunctions.ThrowingFunction.class);

    Return.of("").map(mockMap);
    verify(mockMap).apply(ArgumentMatchers.any());
    reset(mockMap);

    Try.of(() -> "").map(mockMap);
    verify(mockMap).apply(ArgumentMatchers.any());
    reset(mockMap);

    Try.of(() -> {}).map(mockMap);
    verify(mockMap).apply(ArgumentMatchers.any());
    reset(mockMap);

    Throw.of(new Exception()).map(mockMap);
    verifyNoInteractions(mockMap);

    Try.of((TryFunctions.ThrowingSupplier<String>) () -> {
      throw new Exception("oops");
    }).map(mockMap);
    verifyNoInteractions(mockMap);

    Try.of((TryFunctions.ThrowingRunnable) () -> {
      throw new Exception("oops");
    }).map(mockMap);
    verifyNoInteractions(mockMap);
  }

  @Test
  @SuppressWarnings("unchecked")
  void flatMap() throws Exception {
    final var mockMap = mock(TryFunctions.ThrowingFunction.class);

    Return.of("").flatMap(mockMap);
    verify(mockMap).apply(ArgumentMatchers.any());
    reset(mockMap);

    Try.of(() -> "").flatMap(mockMap);
    verify(mockMap).apply(ArgumentMatchers.any());
    reset(mockMap);

    Try.of(() -> {}).flatMap(mockMap);
    verify(mockMap).apply(ArgumentMatchers.any());
    reset(mockMap);

    Throw.of(new Exception()).flatMap(mockMap);
    verifyNoInteractions(mockMap);

    Try.of((TryFunctions.ThrowingSupplier<String>) () -> {
      throw new Exception("oops");
    }).flatMap(mockMap);
    verifyNoInteractions(mockMap);

    Try.of((TryFunctions.ThrowingRunnable) () -> {
      throw new Exception("oops");
    }).flatMap(mockMap);
    verifyNoInteractions(mockMap);
  }
}