package org.folio.holdingsiq.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.hc.core5.http.HttpStatus;
import org.folio.holdingsiq.model.RootProxyCustomLabels;
import org.folio.holdingsiq.model.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProviderHoldingsIQServiceImplTest extends HoldingsIQServiceTestConfig {

  private ProviderHoldingsIQServiceImpl service;

  @BeforeEach
  void setUp() {
    var configuration = getConfiguration();
    var vertx = Vertx.vertx();
    service = new ProviderHoldingsIQServiceImpl(configuration, vertx);
  }

  @Test
  void getVendorId() {
    var rootProxyCustomLabels = RootProxyCustomLabels.builder().vendorId(String.valueOf(VENDOR_ID)).build();
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(Json.encode(rootProxyCustomLabels)))
    );
    var completableFuture = service.getVendorId();

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void retrieveVendors() {
    var urlPattern = new UrlPattern(
      equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors?search=Busket&offset=1&count=5&orderby=vendorname"),
      false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.retrieveProviders("Busket", PAGE_FOR_PARAM, COUNT_FOR_PARAM, Sort.NAME);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void retrieveVendorsCompleteExceptionallyWhenRequestWithError404() {
    var urlPattern = new UrlPattern(
      equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors?search=Busket&offset=1&count=5&orderby=vendorname"),
      false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NOT_FOUND).withBody("{}"))
    );
    var completableFuture = service.retrieveProviders("Busket", PAGE_FOR_PARAM, COUNT_FOR_PARAM, Sort.NAME);

    assertFalse(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void retrieveVendorsCompleteExceptionallyWhenRequestWithError401() {
    var urlPattern = new UrlPattern(
      equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors?search=Busket&offset=1&count=5&orderby=vendorname"),
      false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_UNAUTHORIZED).withBody("{}"))
    );
    var completableFuture = service.retrieveProviders("Busket", PAGE_FOR_PARAM, COUNT_FOR_PARAM, Sort.NAME);

    assertFalse(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void retrieveVendorsCompleteExceptionallyWhenThrowServiceException() {
    var urlPattern = new UrlPattern(
      equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors?search=Busket&offset=1&count=5&orderby=vendorname"),
      false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("invalid-json"))
    );
    var completableFuture = service.retrieveProviders("Busket", PAGE_FOR_PARAM, COUNT_FOR_PARAM, Sort.NAME);

    assertFalse(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void updateVendor() {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors/" + VENDOR_ID), false);
    wm.stubFor(
      put(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT).withBody("{}"))
    );
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.updateProvider(VENDOR_ID, vendorPut);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }
}
