package org.folio.holdingsiq.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.vertx.core.Context;

import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.ConfigurationError;
import org.folio.holdingsiq.model.RequestContext;

public interface ConfigurationService {

  CompletableFuture<Configuration> retrieveConfiguration(RequestContext requestContext);

  CompletableFuture<List<ConfigurationError>> verifyCredentials(Configuration configuration, Context vertxContext,
                                                                RequestContext requestContext);
}
