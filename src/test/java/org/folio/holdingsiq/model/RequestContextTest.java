package org.folio.holdingsiq.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.folio.okapi.common.XOkapiHeaders;
import org.junit.jupiter.api.Test;

class RequestContextTest {

  @Test
  void okapiDataInitialization() {
    Map<String, String> headers = new HashMap<>();
    headers.put(XOkapiHeaders.TOKEN, "test-token");
    headers.put(XOkapiHeaders.TENANT, "test-tenant");
    headers.put(XOkapiHeaders.USER_ID, "test-user-id");
    headers.put(XOkapiHeaders.URL, "http://localhost:9130");

    RequestContext requestContext = new RequestContext(headers);

    assertEquals("test-token", requestContext.getToken());
    assertEquals("test-tenant", requestContext.getTenant());
    assertEquals("test-user-id", requestContext.getUserId());
    assertEquals("http://localhost:9130", requestContext.getUrl());
    assertEquals("localhost", requestContext.getHost());
    assertEquals(9130, requestContext.getPort());
  }

  @Test
  void okapiDataInitializationWithInvalidUrl() {
    Map<String, String> headers = new HashMap<>();
    headers.put(XOkapiHeaders.URL, "invalid-url");

    assertThrows(IllegalArgumentException.class, () -> new RequestContext(headers));
  }

  @Test
  void okapiDataInitializationWithDefaultPort() {
    Map<String, String> headers = new HashMap<>();
    headers.put(XOkapiHeaders.URL, "http://localhost");

    RequestContext requestContext = new RequestContext(headers);

    assertEquals("localhost", requestContext.getHost());
    assertEquals(80, requestContext.getPort());
  }

  @Test
  void okapiDataInitializationWithCaseInsensitiveHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("x-okapi-token", "test-token");
    headers.put("x-okapi-tenant", "test-tenant");
    headers.put("x-okapi-user-id", "test-user-id");
    headers.put("x-okapi-url", "http://localhost:9130");

    RequestContext requestContext = new RequestContext(headers);

    assertEquals("test-token", requestContext.getToken());
    assertEquals("test-tenant", requestContext.getTenant());
    assertEquals("test-user-id", requestContext.getUserId());
    assertEquals("http://localhost:9130", requestContext.getUrl());
    assertEquals("localhost", requestContext.getHost());
    assertEquals(9130, requestContext.getPort());
  }
}