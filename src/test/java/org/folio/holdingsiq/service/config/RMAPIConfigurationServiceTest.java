package org.folio.holdingsiq.service.config;

import static io.vertx.core.Future.succeededFuture;
import static io.vertx.core.http.HttpHeaders.*;
import static org.apache.hc.core5.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.folio.holdingsiq.service.config.ConfigTestData.OKAPI_DATA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.service.impl.ConfigurationServiceImpl;
import org.folio.okapi.common.XOkapiHeaders;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RMAPIConfigurationServiceTest {

  private static final String JSON_API_TYPE = "application/vnd.api+json";
  private static final String USER_CREDS_URL = "/eholdings/user-kb-credential";

  private static final String USER_CRED_ID = "8d5b862b-9fb3-411d-afa7-406151739d26";
  private static final String USER_CRED_NAME = "University of Massachusetts";
  private static final String USER_CRED_APIKEY = "APIKEY";
  private static final String USER_CRED_CUSTOMERID = "CUSTID";
  private static final String USER_CRED_URL = "URL";

  @Mock
  private Vertx vertx;
  @Mock
  private WebClient webClient;
  @Mock
  private HttpResponse<Buffer> httpResponse;
  @Mock
  private HttpRequest<Buffer> httpRequest;
  @Mock
  private MultiMap headers;
  private ConfigurationServiceImpl service;

  @Before
  public void setUp() throws Exception {
    openMocks(this).close();

    try (var mocked = mockStatic(WebClient.class)) {
      mocked.when(() -> WebClient.create(vertx)).thenReturn(webClient);
      service = new ConfigurationServiceImpl(vertx);
    }
  }

  @Test
  public void shouldReturnConfiguration() throws ExecutionException, InterruptedException {
    mockCredentialsRequest();

    when(httpResponse.statusCode()).thenReturn(SC_OK);
    when(httpResponse.bodyAsJsonObject()).thenReturn(CredentialsBuilder.instance()
      .id(USER_CRED_ID)
      .name(USER_CRED_NAME)
      .apiKey(USER_CRED_APIKEY)
      .customerId(USER_CRED_CUSTOMERID)
      .url(USER_CRED_URL)
      .build());

    CompletableFuture<Configuration> result = service.retrieveConfiguration(OKAPI_DATA);

    Configuration conf = result.get();

    assertNotNull(conf);
    assertEquals(USER_CRED_CUSTOMERID, conf.getCustomerId());
    assertEquals(USER_CRED_APIKEY, conf.getApiKey());
    assertEquals(USER_CRED_URL, conf.getUrl());

    verifyCredentialsRequest();
  }

  @Test
  public void shouldReturnPartiallyInitializedConfiguration() throws ExecutionException, InterruptedException {
    mockCredentialsRequest();

    when(httpResponse.statusCode()).thenReturn(SC_OK);
    when(httpResponse.bodyAsJsonObject()).thenReturn(CredentialsBuilder.instance()
      .id(USER_CRED_ID)
      .name(USER_CRED_NAME)
      .customerId(USER_CRED_CUSTOMERID)
      .build());

    CompletableFuture<Configuration> result = service.retrieveConfiguration(OKAPI_DATA);

    Configuration conf = result.get();

    assertNotNull(conf);
    assertEquals(USER_CRED_CUSTOMERID, conf.getCustomerId());
    assertNull(USER_CRED_APIKEY, conf.getApiKey());
    assertNull(USER_CRED_URL, conf.getUrl());

    verifyCredentialsRequest();
  }

  @Test
  public void shouldFailIfUserCredentialsResponseIsNotOk() {
    mockCredentialsRequest();

    when(httpResponse.statusCode()).thenReturn(SC_INTERNAL_SERVER_ERROR);
    when(httpResponse.toString()).thenReturn("failure");

    CompletableFuture<Configuration> result = service.retrieveConfiguration(OKAPI_DATA);

    assertTrue(result.isCompletedExceptionally());

    verifyCredentialsRequest();
  }

  private void verifyCredentialsRequest() {
    verify(webClient).get(OKAPI_DATA.getOkapiPort(), OKAPI_DATA.getOkapiHost(), USER_CREDS_URL);
    verify(httpRequest).putHeader(XOkapiHeaders.TENANT, OKAPI_DATA.getTenant());
    verify(httpRequest).putHeader(XOkapiHeaders.TOKEN, OKAPI_DATA.getApiToken());
    verify(httpRequest).putHeader(XOkapiHeaders.USER_ID, OKAPI_DATA.getUserId());
    verify(httpRequest).putHeader(ACCEPT.toString(), JSON_API_TYPE);
    verify(httpRequest).send();
  }

  private void mockCredentialsRequest() {
    when(webClient.get(OKAPI_DATA.getOkapiPort(), OKAPI_DATA.getOkapiHost(), USER_CREDS_URL)).thenReturn(httpRequest);

    when(httpRequest.putHeader(XOkapiHeaders.TENANT, OKAPI_DATA.getTenant())).thenReturn(httpRequest);
    when(httpRequest.putHeader(XOkapiHeaders.TOKEN, OKAPI_DATA.getApiToken())).thenReturn(httpRequest);
    when(httpRequest.putHeader(XOkapiHeaders.USER_ID, OKAPI_DATA.getUserId())).thenReturn(httpRequest);
    when(httpRequest.putHeader(ACCEPT.toString(), JSON_API_TYPE)).thenReturn(httpRequest);
    when(httpResponse.headers()).thenReturn(headers);
    when(headers.get(HttpHeaders.CONTENT_TYPE)).thenReturn(JSON_API_TYPE);
    when(httpRequest.send()).thenReturn(succeededFuture(httpResponse));
  }

  private static class CredentialsBuilder {

    private final JsonObject creds;

    private CredentialsBuilder() {
      creds = new JsonObject();
    }

    static CredentialsBuilder instance() {
      return new CredentialsBuilder();
    }

    CredentialsBuilder id(String id) {
      if (StringUtils.isNotBlank(id)) {
        creds.put("id", id);
      }

      return this;
    }

    CredentialsBuilder name(String name) {
      if (StringUtils.isNotBlank(name)) {
        attributes().put("name", name);
      }

      return this;
    }

    CredentialsBuilder apiKey(String apiKey) {
      if (StringUtils.isNotBlank(apiKey)) {
        attributes().put("apiKey", apiKey);
      }

      return this;
    }

    CredentialsBuilder url(String url) {
      if (StringUtils.isNotBlank(url)) {
        attributes().put("url", url);
      }

      return this;
    }

    CredentialsBuilder customerId(String customerId) {
      if (StringUtils.isNotBlank(customerId)) {
        attributes().put("customerId", customerId);
      }

      return this;
    }

    JsonObject build() {
      return creds;
    }

    private JsonObject attributes() {
      JsonObject attrs = creds.getJsonObject("attributes");

      if (attrs == null) {
        attrs = new JsonObject();
        creds.put("attributes", attrs);
      }

      return attrs;
    }
  }
}
