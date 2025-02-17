package org.folio.holdingsiq.service.impl;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.ConfigurationError;
import org.folio.holdingsiq.model.OkapiData;
import org.folio.holdingsiq.service.ConfigurationService;
import org.folio.holdingsiq.service.exception.ConfigurationServiceException;
import org.folio.holdingsiq.service.exception.ServiceResponseException;
import org.folio.okapi.common.XOkapiHeaders;

/**
 * Retrieves the RM API connection details from eHoldings.
 */
public class ConfigurationServiceImpl implements ConfigurationService {

  private static final Logger LOG = LogManager.getLogger(ConfigurationServiceImpl.class);

  private static final String JSON_API_TYPE = "application/vnd.api+json";
  private static final String USER_CREDS_URL = "/eholdings/user-kb-credential";

  private final WebClient client;

  public ConfigurationServiceImpl(Vertx vertx) {
    this.client = WebClient.create(vertx);
  }

  @Override
  public CompletableFuture<Configuration> retrieveConfiguration(OkapiData okapiData) {
    return getUserCredentials(okapiData).thenApply(this::credentialsToConfiguration);
  }

  @Override
  public CompletableFuture<List<ConfigurationError>> verifyCredentials(Configuration configuration,
                                                                       Context vertxContext, OkapiData okapiData) {
    List<ConfigurationError> errors = new ArrayList<>();

    if (!isConfigurationParametersValid(configuration, errors)) {
      return completedFuture(errors);
    }

    CompletableFuture<List<ConfigurationError>> resultFuture = new CompletableFuture<>();

    new HoldingsIQServiceImpl(configuration, vertxContext.owner())
      .verifyCredentials()
      .thenAccept(o -> resultFuture.complete(Collections.emptyList()))
      .exceptionally(exception -> {
        if (isInvalidConfigurationException(exception)) {
          resultFuture.complete(Collections.singletonList(new ConfigurationError("KB API Credentials are invalid")));
        } else {
          resultFuture.completeExceptionally(exception.getCause());
        }
        return null;
      });

    return resultFuture;
  }

  private boolean isInvalidConfigurationException(Throwable exception) {
    Throwable cause = exception.getCause();
    return cause instanceof UnknownHostException || (cause instanceof ServiceResponseException
                                                     && isInvalidConfigurationStatusCode(
      ((ServiceResponseException) cause).getCode()));
  }

  private boolean isInvalidConfigurationStatusCode(Integer statusCode) {
    return statusCode == 401 || statusCode == 403 || statusCode == 404;
  }

  private CompletableFuture<JsonObject> getUserCredentials(OkapiData okapiData) {
    return mapVertxFuture(getCredentialsJson(okapiData)).whenComplete(this::logCredentialsRetrievalResult);
  }

  private <T> CompletableFuture<T> mapVertxFuture(Future<T> future) {
    CompletableFuture<T> completableFuture = new CompletableFuture<>();

    future
      .map(completableFuture::complete)
      .otherwise(completableFuture::completeExceptionally);

    return completableFuture;
  }

  private void logCredentialsRetrievalResult(JsonObject creds, Throwable t) {
    if (t != null) {
      LOG.info("Failed to retrieve user credentials: " + t);
    } else {
      CredentialsReader reader = CredentialsReader.from(creds);

      LOG.info("User credentials retrieved: id = '" + reader.getId() + "', " +
               "name = '" + reader.getName() + "'");
    }
  }

  private Future<JsonObject> getCredentialsJson(OkapiData okapiData) {
    Promise<HttpResponse<Buffer>> promise = Promise.promise();

    client.get(okapiData.getOkapiPort(), okapiData.getOkapiHost(), USER_CREDS_URL)
      .putHeader(XOkapiHeaders.TENANT, okapiData.getTenant())
      .putHeader(XOkapiHeaders.TOKEN, okapiData.getApiToken())
      .putHeader(XOkapiHeaders.USER_ID, okapiData.getUserId())
      .putHeader(HttpHeaders.ACCEPT.toString(), JSON_API_TYPE)
      .expect(ResponsePredicate.contentType(JSON_API_TYPE))
      .send(promise);

    return promise.future().compose(res ->
      res.statusCode() == HttpResponseStatus.OK.code()
      ? succeededFuture(res.bodyAsJsonObject())
      : failedFuture(new ConfigurationServiceException(res.bodyAsString(), res.statusCode())));
  }

  private Configuration credentialsToConfiguration(JsonObject creds) {
    Configuration.ConfigurationBuilder builder = Configuration.builder();

    CredentialsReader reader = CredentialsReader.from(creds);

    builder.customerId(reader.getCustomerId());
    builder.apiKey(reader.getApiKey());
    builder.url(reader.getUrl());

    return builder.build();
  }

  private boolean isConfigurationParametersValid(Configuration configuration, List<ConfigurationError> errors) {
    if (isEmpty(configuration.getUrl())) {
      errors.add(new ConfigurationError("Url is empty"));
    }
    if (isEmpty(configuration.getApiKey())) {
      errors.add(new ConfigurationError("API key is empty"));
    }
    if (isEmpty(configuration.getCustomerId())) {
      errors.add(new ConfigurationError("Customer ID is empty"));
    }

    UrlValidator urlValidator = new UrlValidator(ALLOW_LOCAL_URLS);
    if (!urlValidator.isValid(configuration.getUrl())) {
      errors.add(new ConfigurationError("Url is invalid"));
    }

    return errors.isEmpty();
  }

  private static class CredentialsReader {

    private static final JsonObject EMPTY_JSON = new JsonObject();

    private String id;
    private JsonObject attrs;

    CredentialsReader(JsonObject json) {
      if (json != null) {
        id = json.getString("id");
        attrs = json.getJsonObject("attributes", EMPTY_JSON);
      } else {
        attrs = EMPTY_JSON;
      }
    }

    static CredentialsReader from(JsonObject json) {
      return new CredentialsReader(json);
    }

    String getId() {
      return id;
    }

    String getName() {
      return attrs.getString("name");
    }

    String getApiKey() {
      return attrs.getString("apiKey");
    }

    String getUrl() {
      return attrs.getString("url");
    }

    String getCustomerId() {
      return attrs.getString("customerId");
    }
  }

}
