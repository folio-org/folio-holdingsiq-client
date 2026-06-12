package org.folio.holdingsiq.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static org.folio.holdingsiq.service.util.DataUtils.getResourceBody;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.vertx.core.Vertx;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.HttpStatus;
import org.folio.holdingsiq.model.ResourceSelectedPayload;
import org.folio.holdingsiq.service.ResourcesHoldingsIQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResourcesHoldingsIQServiceImplTest extends HoldingsIQServiceTestConfig {

  private static final String URL = "/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors/" +
    VENDOR_ID + "/packages/" + PACKAGE_ID + "/titles/" + TITLE_ID;
  private ResourcesHoldingsIQService service;


  @BeforeEach
  void setUp() {
    service = new ResourcesHoldingsIQServiceImpl(getConfiguration(), Vertx.vertx());
  }

  @Test
  void postResources() {
    ResourceSelectedPayload resourceSelectedPayload = new ResourceSelectedPayload(false, "titleName",
      "pubType", STUB_BASE_URL);
    var urlPattern = new UrlPattern(equalTo(URL), false);
    wm.stubFor(
      put(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT))
    );
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.postResource(resourceSelectedPayload, resourceId);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  @SneakyThrows
  void retrieveResources() {
    var urlPattern = new UrlPattern(equalTo(URL), false);

    wm.stubFor(get(urlPattern)
      .willReturn(aResponse().withStatus(HttpStatus.SC_OK)
        .withBody(getResourceBody())));

    var completableFuture = service.retrieveResource(resourceId);
    assertTrue(isCompletedNormally(completableFuture));
    assertNotNull(completableFuture.get().getCustomerResourcesList().getFirst().getProxy().getProxiedUrl());
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void updateResources() {
    var urlPattern = new UrlPattern(equalTo(URL), false);
    wm.stubFor(
      put(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT))
    );
    var completableFuture = service.updateResource(resourceId, resourcePut);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern));
  }

  @Test
  void deleteResource() {
    var urlPattern = new UrlPattern(equalTo(URL), false);
    wm.stubFor(
      put(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT))
    );
    var completableFuture = service.deleteResource(resourceId);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern));
  }
}
