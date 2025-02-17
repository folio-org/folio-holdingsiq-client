package org.folio.holdingsiq.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.folio.okapi.common.XOkapiHeaders;
import org.junit.Test;

public class OkapiDataTest {

  @Test
  public void testOkapiDataInitialization() {
    Map<String, String> headers = new HashMap<>();
    headers.put(XOkapiHeaders.TOKEN, "test-token");
    headers.put(XOkapiHeaders.TENANT, "test-tenant");
    headers.put(XOkapiHeaders.USER_ID, "test-user-id");
    headers.put(XOkapiHeaders.URL, "http://localhost:9130");

    OkapiData okapiData = new OkapiData(headers);

    assertEquals("test-token", okapiData.getApiToken());
    assertEquals("test-tenant", okapiData.getTenant());
    assertEquals("test-user-id", okapiData.getUserId());
    assertEquals("http://localhost:9130", okapiData.getOkapiUrl());
    assertEquals("localhost", okapiData.getOkapiHost());
    assertEquals(9130, okapiData.getOkapiPort());
  }

  @Test
  public void testOkapiDataInitializationWithInvalidUrl() {
    Map<String, String> headers = new HashMap<>();
    headers.put(XOkapiHeaders.URL, "invalid-url");

    assertThrows(IllegalArgumentException.class, () -> new OkapiData(headers));
  }

  @Test
  public void testOkapiDataInitializationWithDefaultPort() {
    Map<String, String> headers = new HashMap<>();
    headers.put(XOkapiHeaders.URL, "http://localhost");

    OkapiData okapiData = new OkapiData(headers);

    assertEquals("localhost", okapiData.getOkapiHost());
    assertEquals(80, okapiData.getOkapiPort());
  }

  @Test
  public void testOkapiDataInitializationWithCaseInsensitiveHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("x-okapi-token", "test-token");
    headers.put("x-okapi-tenant", "test-tenant");
    headers.put("x-okapi-user-id", "test-user-id");
    headers.put("x-okapi-url", "http://localhost:9130");

    OkapiData okapiData = new OkapiData(headers);

    assertEquals("test-token", okapiData.getApiToken());
    assertEquals("test-tenant", okapiData.getTenant());
    assertEquals("test-user-id", okapiData.getUserId());
    assertEquals("http://localhost:9130", okapiData.getOkapiUrl());
    assertEquals("localhost", okapiData.getOkapiHost());
    assertEquals(9130, okapiData.getOkapiPort());
  }
}