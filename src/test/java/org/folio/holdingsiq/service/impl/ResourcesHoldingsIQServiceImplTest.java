package org.folio.holdingsiq.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.vertx.core.Vertx;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.HttpStatus;
import org.folio.holdingsiq.model.ResourceSelectedPayload;
import org.folio.holdingsiq.model.Visibility;
import org.folio.holdingsiq.service.ResourcesHoldingsIQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResourcesHoldingsIQServiceImplTest extends HoldingsIQServiceTestConfig {

  private static final String URL = "/rm/rmaccounts/v2/" + STUB_CUSTOMER_ID + "/lists/" + PACKAGE_ID
                                    + "/titles/" + TITLE_ID;
  private ResourcesHoldingsIQService service;

  @BeforeEach
  void setUp() {
    service = new ResourcesHoldingsIQServiceImpl(getConfiguration(), Vertx.vertx());
  }

  @Test
  @SneakyThrows
  void postResources() {
    ResourceSelectedPayload resourceSelectedPayload = new ResourceSelectedPayload(false, "titleName",
      "pubType", STUB_BASE_URL);
    var urlPattern = new UrlPattern(equalTo(URL), false);
    wm.stubFor(
      put(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT))
    );
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(getJson("resource-v2.json")))
    );
    var completableFuture = service.postResource(resourceSelectedPayload, resourceId);

    assertTrue(isCompletedNormally(completableFuture));
    assertEquals(TITLE_ID, completableFuture.get().getTitleId());
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern)
      .withRequestBody(matchingJsonPath("$.isSelected", equalTo("false"))));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  @SneakyThrows
  void retrieveResource() {
    var urlPattern = new UrlPattern(equalTo(URL), false);

    wm.stubFor(get(urlPattern)
      .willReturn(aResponse().withStatus(HttpStatus.SC_OK)
        .withBody(getJson("resource-v2.json"))));

    var completableFuture = service.retrieveResource(resourceId);
    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));

    var resource = completableFuture.get();
    assertEquals(TITLE_ID, resource.getTitleId());
    assertEquals("A Tale of Two Cities", resource.getTitleName());
    assertEquals("Journal", resource.getPubType());
    assertEquals("2nd", resource.getEdition());
    assertTrue(resource.getIsPeerReviewed());
    assertEquals("Smith, John", resource.getContributorsList().getFirst().getTitleContributor());
    assertEquals("Abbreviated", resource.getAlternateTitleList().getFirst().getTitleType());
    assertEquals(1, resource.getTitleRelationships().size());

    assertEquals(PACKAGE_ID, resource.getPackageId());
    assertEquals(VENDOR_ID, resource.getVendorId());
    assertEquals(123, resource.getLocationId());
    assertTrue(resource.getIsSelected());
    assertNotNull(resource.getProxy().getProxiedUrl());
    assertEquals("guest", resource.getProxy().getId());
    assertFalse(resource.getProxy().getInherited());
    assertEquals(List.of(new Visibility("pf", true, "Hidden by customer"), new Visibility("marc", false, "")),
      resource.getVisibilityDetails());
    assertEquals("2005-01-01", resource.getCustomCoverageList().getFirst().getBeginCoverage());
    assertEquals(10, resource.getCustomEmbargoPeriod().getEmbargoValue());
    assertEquals(3, resource.getManagedEmbargoPeriod().getEmbargoValue());
    assertTrue(resource.getKbartExcluded());
    assertEquals("KERIS", resource.getConsortia());
    assertEquals("Sample Text 1", resource.getUserDefinedFields().getUserDefinedField1());
    assertEquals("Sample Text 5", resource.getUserDefinedFields().getUserDefinedField5());
  }

  @Test
  @SneakyThrows
  void retrieveResourcesByPackage() {
    var urlPattern = new UrlPattern(equalTo(
      "/rm/rmaccounts/v2/" + STUB_CUSTOMER_ID + "/lists/" + PACKAGE_ID
      + "/titles?searchfield=titlename&selection=all"
      + "&resourcetype=all&search=&offset=1&count=5&orderby=titlename"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody(getJson("list-titles-v2.json")))
    );
    var completableFuture = service.retrieveResources(PACKAGE_ID, resourceFilter, pageable);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));

    var result = completableFuture.get();
    assertEquals(1, result.getTotalResults());
    assertEquals(1, result.getTitles().size());
    var resource = result.getTitles().getFirst();
    assertEquals(TITLE_ID, resource.getTitleId());
    assertEquals("A Tale of Two Cities", resource.getTitleName());
    assertEquals("AtoZ", resource.getIdentifiersList().getFirst().getSource());
    assertEquals(PACKAGE_ID, resource.getPackageId());
    assertEquals(VENDOR_ID, resource.getVendorId());
    assertEquals(777, resource.getLocationId());
    assertTrue(resource.getIsSelected());
    assertEquals("pf", resource.getVisibilityDetails().getFirst().category());
    assertEquals("https://search.ebscohost.com/title/3333", resource.getUrl());
  }

  @Test
  void retrieveResourceWithRelationships() {
    var urlPattern = new UrlPattern(equalTo(URL + "?includerelationships=true"), false);
    wm.stubFor(get(urlPattern)
      .willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}")));

    var completableFuture = service.retrieveResourceWithRelationships(resourceId);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void updateResources() {
    var urlPattern = new UrlPattern(equalTo(URL), false);
    wm.stubFor(
      put(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT))
    );
    var resourcePutBody = resourcePut.toBuilder()
      .visibilityDetails(List.of(Visibility.builder().category("pf").hidden(true).build()))
      .build();
    var completableFuture = service.updateResource(resourceId, resourcePutBody);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern)
      .withRequestBody(matchingJsonPath("$.visibility[0].category", equalTo("pf")))
      .withRequestBody(matchingJsonPath("$.visibility[0].hidden", equalTo("true"))));
  }

  @Test
  void deleteResource() {
    var urlPattern = new UrlPattern(equalTo(URL), false);
    wm.stubFor(
      put(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_NO_CONTENT))
    );
    var completableFuture = service.deleteResource(resourceId);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern)
      .withRequestBody(matchingJsonPath("$.isSelected", equalTo("false"))));
  }
}
