package org.folio.holdingsiq.service.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import io.vertx.core.Context;

import lombok.extern.log4j.Log4j2;
import org.folio.cache.VertxCache;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.ConfigurationError;
import org.folio.holdingsiq.model.RequestContext;
import org.folio.holdingsiq.service.ConfigurationService;

@Log4j2
@RequiredArgsConstructor
public class ConfigurationServiceCache implements ConfigurationService {

  private final ConfigurationService configurationService;

  private final VertxCache<String, Configuration> configurationCache;

  @Override
  public CompletableFuture<Configuration> retrieveConfiguration(RequestContext requestContext) {
    return configurationCache.getValueOrLoad(
      requestContext.getUserId(),
      () -> configurationService.retrieveConfiguration(requestContext));
  }

  @Override
  public CompletableFuture<List<ConfigurationError>> verifyCredentials(Configuration configuration, Context vertxContext,
                                                                       RequestContext requestContext) {
    if (configuration.getConfigValid() != null && configuration.getConfigValid()) {
      return completedFuture(emptyList());
    }

    return configurationService.verifyCredentials(configuration, vertxContext, requestContext)
      .thenCompose(errors -> {
        if (!errors.isEmpty()) {
          return completedFuture(errors);
        }

        return storeConfigurationInCache(configuration, requestContext)
          .thenApply(v -> Collections.<ConfigurationError>emptyList())
          .exceptionally(t -> singletonList(new ConfigurationError(t.getMessage())));
      });
  }

  private CompletableFuture<Void> storeConfigurationInCache(Configuration config, RequestContext requestContext) {
    if (requestContext.getUserId() == null || requestContext.getUserId().isEmpty()) {
      log.warn("storeConfigurationInCache:: User id is empty");
      return CompletableFuture.failedFuture(new IllegalArgumentException("User id is empty"));
    }

    configurationCache.putValue(requestContext.getUserId(), config.toBuilder().configValid(true).build());
    return CompletableFuture.completedFuture(null);
  }

}
