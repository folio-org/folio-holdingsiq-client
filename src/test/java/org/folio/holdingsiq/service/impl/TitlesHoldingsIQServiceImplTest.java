package org.folio.holdingsiq.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.hc.core5.http.HttpStatus;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.service.TitlesHoldingsIQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitlesHoldingsIQServiceImplTest extends HoldingsIQServiceTestConfig {

  private TitlesHoldingsIQService service;

  @BeforeEach
  void setUp() {
    service = new TitlesHoldingsIQServiceImpl(getConfiguration(), Vertx.vertx());
  }

  @Test
  void retrieveTitles() {
    var urlPattern = new UrlPattern(equalTo(
      "/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/titles?searchfield=titlename&selection=all&resourcetype=all&searchtype="
      + "advanced&packageidfilter=123,23&search=&offset=1&count=5&orderby=titlename"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(Json.encode(titles)))
    );
    var completableFuture = service.retrieveTitles(filterQueryWithPackageIds, null,
      Sort.NAME, PAGE_FOR_PARAM, COUNT_FOR_PARAM);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void retrieveTitlesWithVendorId() {
    var urlPattern = new UrlPattern(equalTo(
      "/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors/" + VENDOR_ID + "/packages/" + PACKAGE_ID
      + "/titles?searchfield=titlename&selection=all"
      + "&resourcetype=all&searchtype=advanced&search=&offset=1&count=5&orderby=titlename"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(Json.encode(titles)))
    );
    var completableFuture =
      service.retrieveTitles(VENDOR_ID, PACKAGE_ID, filterQuery, null, Sort.NAME, PAGE_FOR_PARAM, COUNT_FOR_PARAM);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void retrieveTitle() {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/titles/" + TITLE_ID), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.retrieveTitle(TITLE_ID);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void postTitle() {
    var urlPatternPost = new UrlPattern(
      equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors/" + VENDOR_ID + "/packages/" + PACKAGE_ID + "/titles"),
      false);
    var urlPatternGet = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/titles/" + TITLE_ID), false);
    wm.stubFor(
      get(urlPatternGet).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(Json.encode(titleCreated)))
    );
    wm.stubFor(
      post(urlPatternPost).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(Json.encode(titleCreated)))
    );
    var completableFuture = service.postTitle(titlePost, packageId);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.POST, urlPatternPost));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPatternGet));
  }
}
