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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.folio.cache.VertxCache;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.ConfigurationError;
import org.folio.holdingsiq.model.OkapiData;
import org.folio.holdingsiq.service.ConfigurationService;
import org.folio.holdingsiq.service.impl.ConfigurationServiceCache;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ConfigurationServiceCacheTest {

  private static final Configuration STUB_CONFIGURATION = Configuration.builder().build();

  @Mock
  private Context context;
  @Mock
  private ConfigurationService configService;
  private VertxCache<String, Configuration> testCache;
  private ConfigurationServiceCache cacheService;

  @Before
  public void setUp() throws Exception {
    openMocks(this).close();

    testCache = new VertxCache<>(Vertx.vertx(), 60, "testCache");
    cacheService = new ConfigurationServiceCache(configService, testCache);
  }

  @Test
  public void shouldDelegateToOtherServiceOnCacheMiss() throws ExecutionException, InterruptedException {
    when(configService.retrieveConfiguration(OKAPI_DATA)).thenReturn(completedFuture(STUB_CONFIGURATION));

    Configuration config = cacheService.retrieveConfiguration(OKAPI_DATA).get();

    assertThat(config, sameInstance(STUB_CONFIGURATION));
    verify(configService).retrieveConfiguration(OKAPI_DATA);
  }

  @Test
  public void shouldUseCachedValueOnCacheHit() throws ExecutionException, InterruptedException {
    testCache.putValue(USER_ID, STUB_CONFIGURATION);

    Configuration config = cacheService.retrieveConfiguration(OKAPI_DATA).get();

    assertThat(config, sameInstance(STUB_CONFIGURATION));
    verifyNoInteractions(configService);
  }

  @Test
  public void shouldReturnValidConfigurationImmediately() throws ExecutionException, InterruptedException {
    List<ConfigurationError> errors = cacheService.verifyCredentials(Configuration.builder().configValid(true).build(),
      context, OKAPI_DATA).get();

    assertThat(errors, Matchers.empty());

    verifyNoInteractions(configService);
  }

  @Test
  public void shouldReturnVerificationErrorsFromService() throws ExecutionException, InterruptedException {
    ConfigurationError error = new ConfigurationError("ERROR");
    when(configService.verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA))
      .thenReturn(completedFuture(Collections.singletonList(error)));

    List<ConfigurationError> errors = cacheService.verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA).get();

    assertThat(errors, contains(error));

    verify(configService).verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA);
  }

  @Test
  public void shouldStoreVerifiedConfigurationInCache() throws ExecutionException, InterruptedException {
    when(configService.verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA)).thenReturn(
      completedFuture(emptyList()));

    List<ConfigurationError> errors = cacheService.verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA).get();

    assertThat(errors, empty());
    assertThat(testCache.getValue(USER_ID),
      equalTo(STUB_CONFIGURATION.toBuilder().configValid(Boolean.TRUE).build()));

    verify(configService).verifyCredentials(STUB_CONFIGURATION, context, OKAPI_DATA);
  }

  @Test
  public void shouldReturnTokenExceptionAsVerificationError() throws ExecutionException, InterruptedException {
    var okapiData = new OkapiData(ImmutableMap.of(
      OKAPI_TOKEN_HEADER, "token",
      OKAPI_TENANT_HEADER, "tenant",
      OKAPI_URL_HEADER, "https://localhost:8080"));
    when(configService.verifyCredentials(STUB_CONFIGURATION, context, okapiData)).thenReturn(
      completedFuture(emptyList()));

    List<ConfigurationError> errors = cacheService.verifyCredentials(STUB_CONFIGURATION, context, okapiData).get();

    assertThat(errors, hasSize(1));
    assertThat(errors.getFirst().getMessage(), containsString("User id is empty"));
    assertThat(testCache.getValue(USER_ID), nullValue());

    verify(configService).verifyCredentials(STUB_CONFIGURATION, context, okapiData);
  }
}
