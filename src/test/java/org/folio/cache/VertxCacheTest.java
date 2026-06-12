package org.folio.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import io.vertx.core.Vertx;

class VertxCacheTest {

  private static final String KEY = "key";
  private static final String VALUE = "value";
  private final VertxCache<String, String> testCache = new VertxCache<>(Vertx.vertx(), 60, "testCache");

  @Test
  void shouldInitiallyReturnNull() {
    assertNull(testCache.getValue(KEY));
  }

  @Test
  void shouldCacheValueAfterPut() {
    testCache.putValue(KEY, VALUE);
    assertEquals(VALUE, testCache.getValue(KEY));
  }

  @Test
  void shouldLoadValueOnCacheMiss() {
    Supplier<CompletableFuture<String>> loader = spy(new TestProducer());
    CompletableFuture<String> returnedValue = testCache.getValueOrLoad(KEY, loader);
    assertEquals(VALUE, testCache.getValue(KEY));
    assertEquals(VALUE, returnedValue.join());
    verify(loader).get();
  }

  @Test
  void shouldNotLoadValueOnCacheHit() {
    Supplier<CompletableFuture<String>> loader = spy(new TestProducer());
    testCache.putValue(KEY, VALUE);
    CompletableFuture<String> returnedValue = testCache.getValueOrLoad(KEY, loader);
    assertEquals(VALUE, testCache.getValue(KEY));
    assertEquals(VALUE, returnedValue.join());
    verifyNoInteractions(loader);
  }

  @Test
  void shouldInvalidateCache() {
    testCache.putValue(KEY, VALUE);
    testCache.invalidateAll();
    assertNull(testCache.getValue(KEY));
  }

  @Test
  void shouldInvalidateCacheByKey() {
    testCache.putValue(KEY, VALUE);
    testCache.invalidate(KEY);
    assertNull(testCache.getValue(KEY));
  }

  private static class TestProducer implements Supplier<CompletableFuture<String>> {
    @Override
    public CompletableFuture<String> get() {
      return CompletableFuture.completedFuture(VALUE);
    }
  }
}
