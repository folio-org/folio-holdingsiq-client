package org.folio.holdingsiq.service.config;

import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.folio.holdingsiq.service.config.ConfigTestData.OKAPI_DATA;
import static org.folio.holdingsiq.service.config.ConfigTestData.OKAPI_TENANT_HEADER;
import static org.folio.holdingsiq.service.config.ConfigTestData.OKAPI_TOKEN_HEADER;
import static org.folio.holdingsiq.service.config.ConfigTestData.OKAPI_URL_HEADER;
import static org.folio.holdingsiq.service.config.ConfigTestData.USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import java.util.Collections;
import java.util.List;
import org.folio.cache.VertxCache;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.ConfigurationError;
import org.folio.holdingsiq.model.OkapiData;
import org.folio.holdingsiq.service.ConfigurationService;
import org.folio.holdingsiq.service.impl.ConfigurationServiceCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceCacheTest {

  private static final Configuration STUB_CONFIGURATION = Configuration.builder().build();

  @Mock
  private Context context;
  @Mock
  private ConfigurationService configService;
  private VertxCache<String, Configuration> testCache;
  private ConfigurationServiceCache cacheService;

  @BeforeEach
  void setUp() {
    testCache = new VertxCache<>(Vertx.vertx(), 60, "testCache");
    cacheService = new ConfigurationServiceCache(configService, testCache);
  }

  @Test
  void shouldDelegateToOtherServiceOnCacheMiss() throws Exception {
    when(configService.retrieveConfiguration(OKAPI_DATA)).thenReturn(completedFuture(STUB_CONFIGURATION));

    Configuration config = cacheService.retrieveConfiguration(OKAPI_DATA).get();

    assertSame(STUB_CONFIGURATION, config);
    verify(configService).retrieveConfiguration(OKAPI_DATA);
  }

  @Test
  void shouldUseCachedValueOnCacheHit() throws Exception {
    testCache.putValue(USER_ID, STUB_CONFIGURATION);

    Configuration config = cacheService.retrieveConfiguration(OKAPI_DATA).get();

    assertSame(STUB_CONFIGURATION, config);
    verifyNoInteractions(configService);
  }

  @Test
  void shouldReturnValidConfigurationImmediately() throws Exception {
    List<ConfigurationError> errors = cacheService.verifyCredentials(Configuration.builder().configValid(true).build(),
      context, OKAPI_DATA).get();

    assertTrue(errors.isEmpty());

    verifyNoInteractions(configService);
  }

  @Test
  void shouldReturnVerificationErrorsFromService() throws Exception {
    ConfigurationError error = new ConfigurationError("ERROR");
    when(configService.verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA))
      .thenReturn(completedFuture(Collections.singletonList(error)));

    List<ConfigurationError> errors = cacheService.verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA).get();

    assertThat(errors, contains(error));

    verify(configService).verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA);
  }

  @Test
  void shouldStoreVerifiedConfigurationInCache() throws Exception {
    when(configService.verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA)).thenReturn(
      completedFuture(emptyList()));

    List<ConfigurationError> errors = cacheService.verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA).get();

    assertTrue(errors.isEmpty());
    assertEquals(testCache.getValue(USER_ID), STUB_CONFIGURATION.toBuilder().configValid(Boolean.TRUE).build());

    verify(configService).verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA);
  }

  @Test
  void shouldReturnTokenExceptionAsVerificationError() throws Exception {
    var okapiData = new OkapiData(ImmutableMap.of(
      OKAPI_TOKEN_HEADER, "token",
      OKAPI_TENANT_HEADER, "tenant",
      OKAPI_URL_HEADER, "https://localhost:8080"));
    when(configService.verifyCredentials(STUB_CONFIGURATION, context, okapiData)).thenReturn(
      completedFuture(emptyList()));

    List<ConfigurationError> errors = cacheService.verifyCredentials(STUB_CONFIGURATION, context, okapiData).get();

    assertEquals(1, errors.size());
    assertTrue(errors.getFirst().getMessage().contains("User id is empty"));
    assertNull(testCache.getValue(USER_ID));

    verify(configService).verifyCredentials(STUB_CONFIGURATION, context, okapiData);
  }
}
