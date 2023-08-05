package io.pagratis.scalaforjava;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OptionTest {
  @Test
  void shouldCreateANone() {
    assertInstanceOf(None.class, Option.none());
  }

  @Test
  void shouldCreateANoneFromNull() {
    assertInstanceOf(None.class, Option.ofNullable(null));
  }

  @Test
  void shouldCreateASome() {
    assertInstanceOf(Some.class, Option.of(""));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldCallIfPresentForSome() {
    final var consumer = mock(Consumer.class);
    doNothing().when(consumer).accept(any());

    Some.of(null).ifPresent(consumer);
    verify(consumer).accept(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldNotCallIfPresentForNone() {
    final var consumer = mock(Consumer.class);
    doNothing().when(consumer).accept(any());

    None.of().ifPresent(consumer);
    verifyNoInteractions(consumer);

    Option.ofNullable(null).ifPresent(consumer);
    verifyNoInteractions(consumer);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldCallTheForrectIfPresentOrElse() {
    final var ifPresent = mock(Consumer.class);
    final var orElse = mock(Runnable.class);

    doNothing().when(ifPresent).accept(any());
    doNothing().when(orElse).run();
    None.of().ifPresentOrElse(ifPresent, orElse);
    verifyNoInteractions(ifPresent);
    verify(orElse).run();

    reset(ifPresent, orElse);
    doNothing().when(ifPresent).accept(any());
    doNothing().when(orElse).run();
    Option.ofNullable(null).ifPresentOrElse(ifPresent, orElse);
    verifyNoInteractions(ifPresent);
    verify(orElse).run();

    reset(ifPresent, orElse);
    doNothing().when(ifPresent).accept(any());
    doNothing().when(orElse).run();
    Option.of("").ifPresentOrElse(ifPresent, orElse);
    verify(ifPresent).accept(any());
    verifyNoInteractions(orElse);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldNotCalLFilterForNone() {
    final var filter = mock(Predicate.class);

    None.of().filter(filter);
    verifyNoInteractions(filter);

    Option.ofNullable(null).filter(filter);
    verifyNoInteractions(filter);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldCallFilterForSome() {
    final var filter = mock(Predicate.class);

    when(filter.test(any())).thenReturn(true);
    Option.of("").filter(filter);
    verify(filter).test(any());

    reset(filter);
    when(filter.test(any())).thenReturn(true);
    Some.of("").filter(filter);
    verify(filter).test(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldNotCallMapForNone() {
    final var map = mock(Function.class);

    Option.ofNullable(null).map(map);
    verifyNoInteractions(map);

    None.of().map(map);
    verifyNoInteractions(map);
  }

  @Test
  void shouldCallMapForSome() {
    assertDoesNotThrow(() ->
        assertEquals(
            1,
            Option
                .of("test")
                .map(t -> {
                  assertEquals("test", t);
                  return 1;
                })
                .orElseThrow()
        )
    );
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldNotCallFlatMapForNone() {
    final var flatMap = mock(Function.class);

    Option.ofNullable(null).flatMap(flatMap);
    verifyNoInteractions(flatMap);

    None.of().flatMap(flatMap);
    verifyNoInteractions(flatMap);
  }

  @Test
  void shouldCallFlatMapForSome() {
    assertDoesNotThrow(() ->
        assertEquals(
            1,
            Option
                .of("test")
                .flatMap(t -> {
                  assertEquals("test", t);
                  return Some.of(1);
                })
                .orElseThrow()
        )
    );
  }
}
