package org.folio.holdingsiq.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.HttpStatus;
import org.folio.holdingsiq.service.TitlesHoldingsIQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitlesHoldingsIQServiceImplTest extends HoldingsIQServiceTestConfig {

  private static final String V2_BASE_PATH = "/rm/rmaccounts/v2/" + STUB_CUSTOMER_ID;

  private TitlesHoldingsIQService service;

  @BeforeEach
  void setUp() {
    service = new TitlesHoldingsIQServiceImpl(getConfiguration(), Vertx.vertx());
  }

  @Test
  @SneakyThrows
  void retrieveTitles() {
    var urlPattern = new UrlPattern(equalTo(
      V2_BASE_PATH + "/titles?searchfield=titlename&selection=all&resourcetype=all&searchtype="
      + "advanced&packageidfilter=123,23&search=&offset=1&count=5&orderby=titlename"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(getJson("titles-v2.json")))
    );
    var completableFuture = service.retrieveTitles(titleFilterWithPackageIds, pageable);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));

    var result = completableFuture.get();
    assertEquals(10, result.getTotalResults());
    assertEquals(2, result.getTitleList().size());
    assertEquals(3333, result.getTitleList().getFirst().getTitleId());
    assertTrue(result.getTitleList().get(1).getCustomerResourcesList().isEmpty());
  }

  @Test
  @SneakyThrows
  void retrieveTitle() {
    var urlPattern = new UrlPattern(equalTo(V2_BASE_PATH + "/titles/" + TITLE_ID), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(getJson("title-v2.json")))
    );
    var completableFuture = service.retrieveTitle(TITLE_ID);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));

    var title = completableFuture.get();
    assertEquals(TITLE_ID, title.getTitleId());
    assertEquals("Dickens, Charles", title.getContributorsList().getFirst().getTitleContributor());
    assertNull(title.getTitleRelationships());
    var resource = title.getCustomerResourcesList().getFirst();
    assertEquals(TITLE_ID, resource.getTitleId());
    assertEquals(PACKAGE_ID, resource.getPackageId());
    assertEquals("EZProxy", resource.getProxy().getId());
    assertTrue(resource.getProxy().getInherited());
    assertEquals(2, resource.getVisibilityDetails().size());
    assertTrue(resource.getVisibilityDetails().getFirst().hidden());
    assertTrue(resource.getIsPrimaryPackage());
    assertFalse(resource.getKbartExcluded());
    assertEquals("KERIS", resource.getConsortia());
    assertEquals(6, resource.getManagedEmbargoPeriod().getEmbargoValue());
    assertEquals("udf1", resource.getUserDefinedFields().getUserDefinedField1());
  }

  @Test
  @SneakyThrows
  void retrieveTitleWithRelationships() {
    var urlPattern = new UrlPattern(
      equalTo(V2_BASE_PATH + "/titles/" + TITLE_ID + "?includerelationships=true"), false);
    wm.stubFor(get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
      .withBody(getJson("title-v2-with-relationships.json"))));
    var completableFuture = service.retrieveTitleWithRelationships(TITLE_ID);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));

    var relationship = completableFuture.get().getTitleRelationships().getFirst();
    assertEquals(42, relationship.resourceRelationshipId());
    assertEquals("CONTINUED_BY", relationship.type());
    assertEquals(TITLE_ID, relationship.source().titleId());
    assertEquals(4444, relationship.target().titleId());
    assertEquals("2020-01-01T00:00:00Z", relationship.date());
  }

  @Test
  void postTitle() {
    var urlPatternPost = new UrlPattern(
      equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors/" + VENDOR_ID + "/packages/" + PACKAGE_ID + "/titles"),
      false);
    var urlPatternGet = new UrlPattern(equalTo(V2_BASE_PATH + "/titles/" + TITLE_ID), false);
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
