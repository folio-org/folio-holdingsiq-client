package org.folio.holdingsiq.service.config;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.*;

import static org.folio.holdingsiq.service.config.ConfigTestData.OKAPI_DATA;

import java.util.List;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.folio.holdingsiq.model.ConfigurationError;
import org.folio.holdingsiq.service.ConfigurationService;
import org.folio.holdingsiq.service.exception.ServiceResponseException;
import org.folio.holdingsiq.service.impl.ConfigurationServiceImpl;
import org.folio.holdingsiq.service.impl.HoldingsIQServiceTestConfig;

class ConfigurationServiceImplTest extends HoldingsIQServiceTestConfig {

  private ConfigurationService configService;
  private Context context;

  @BeforeEach
  void setUp() {
    Vertx vertx = Vertx.vertx();
    context = vertx.getOrCreateContext();
    configService = new ConfigurationServiceImpl(vertx);
  }

  @Test
  void shouldNotReturnErrorsOnVerifyWhenCredentialsAreValid() throws Exception {
    var urlPattern = new UrlPattern(WireMock.equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );

    var completableFuture = configService.verifyCredentials(getConfiguration(), context, OKAPI_DATA);

    assertTrue(isCompletedNormally(completableFuture));
    assertTrue(completableFuture.get().isEmpty());
  }

  @Test
  void shouldReturnConfigurationErrorOnVerifyWhenCredentialsAreInvalidWithCode401()
    throws Exception {
    var urlPattern = new UrlPattern(WireMock.equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_UNAUTHORIZED).withBody("{}"))
    );

    var completableFuture = configService.verifyCredentials(getConfiguration(), context, OKAPI_DATA);

    assertTrue(isCompletedNormally(completableFuture));
    List<ConfigurationError> configurationErrors = completableFuture.get();
    assertEquals(1, configurationErrors.size());
    assertInstanceOf(ConfigurationError.class, configurationErrors.getFirst());
  }

  @Test
  void shouldReturnConfigurationErrorOnVerifyWhenCredentialsAreInvalidWithCode403()
    throws Exception {
    var urlPattern = new UrlPattern(WireMock.equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_FORBIDDEN).withBody("{}"))
    );

    var completableFuture = configService.verifyCredentials(getConfiguration(), context, OKAPI_DATA);

    assertTrue(isCompletedNormally(completableFuture));
    List<ConfigurationError> configurationErrors = completableFuture.get();
    assertEquals(1, configurationErrors.size());
    assertInstanceOf(ConfigurationError.class, configurationErrors.getFirst());
  }

  @Test
  void shouldFailedOnVerifyWhenCredentialsAreValidWithCode429() {
    var urlPattern = new UrlPattern(WireMock.equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(429).withBody("""
        {
          "Errors": [
            {
              "Code": 1010,
              "Message": "Too Many Requests.",
              "SubCode": 0
            }
          ]
        }"""))
    );

    var completableFuture = configService.verifyCredentials(getConfiguration(), context, OKAPI_DATA);


    assertFalse(isCompletedNormally(completableFuture));
    try {
      completableFuture.join();
    } catch (Exception throwable) {
      Throwable cause = throwable.getCause();
      assertInstanceOf(ServiceResponseException.class, cause);
      assertEquals(429, ((ServiceResponseException) cause).getCode());
      assertTrue(((ServiceResponseException) cause).getResponseBody().contains("Too Many Requests."));
    }
  }
}
