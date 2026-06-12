package org.folio.holdingsiq.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.hc.core5.http.HttpStatus;
import org.folio.holdingsiq.model.RootProxyCustomLabels;
import org.folio.holdingsiq.service.HoldingsIQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HoldingsIQServiceImplTest extends HoldingsIQServiceTestConfig {

  private HoldingsIQService service;

  @BeforeEach
  void setUp() {
    service = new HoldingsIQServiceImpl(getConfiguration(), Vertx.vertx());
  }

  @Test
  void verifyCredentials() {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.verifyCredentials();

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void retrieveProxies() {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/proxies"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("[]"))
    );
    var completableFuture = service.retrieveProxies();

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void updateRootProxyCustomLabels() {
    var rootProxyCustomLabels = RootProxyCustomLabels.builder().vendorId(String.valueOf(VENDOR_ID)).build();
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/"), false);
    wm.stubFor(
      put(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT))
    );
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(Json.encode(rootProxyCustomLabels)))
    );
    var completableFuture = service.updateRootProxyCustomLabels(rootProxyCustomLabels);

    assertTrue(isCompletedNormally(completableFuture));

    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern));
  }
}
