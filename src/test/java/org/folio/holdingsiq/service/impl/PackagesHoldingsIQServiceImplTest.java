package org.folio.holdingsiq.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.folio.holdingsiq.model.PackageData;
import org.folio.holdingsiq.model.PackageFilter;
import org.folio.holdingsiq.model.PackageFilterSelected;
import org.folio.holdingsiq.model.PackageFilterType;
import org.folio.holdingsiq.model.PackagePut;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.SearchType;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.service.PackagesHoldingsIQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PackagesHoldingsIQServiceImplTest extends HoldingsIQServiceTestConfig {

  private PackagesHoldingsIQService service;

  @BeforeEach
  void setUp() {
    service = new PackagesHoldingsIQServiceImpl(getConfiguration(), Vertx.vertx());
  }

  @Test
  void retrievePackages() {
    var packageFilter = PackageFilter.builder()
      .filterSelected(PackageFilterSelected.ORDERED_THROUGH_EBSCO)
      .filterType(PackageFilterType.ALL)
      .searchType(SearchType.ADVANCED)
      .query("Query")
      .build();
    var pageable = new Pageable(PAGE_FOR_PARAM, COUNT_FOR_PARAM, Sort.NAME);
    var completableFuture = service.retrievePackages(packageFilter, pageable);

    assertTrue(isCompletedNormally(completableFuture));
    var expectedPath = "/rm/rmaccounts/v2/" + STUB_CUSTOMER_ID + "/lists";
    var expectedQuery = "?selection=orderedthroughebsco"
                        + "&contenttype=all"
                        + "&searchtype=advanced"
                        + "&searchfield=name"
                        + "&search=Query"
                        + "&offset=1"
                        + "&count=5"
                        + "&orderby=packagename";
    wm.verify(getRequestedFor(urlEqualTo(expectedPath + expectedQuery)));
  }

  @Test
  void retrievePackagesWithVendorId() {
    var packageFilter = PackageFilter.builder().build();
    var pageable = new Pageable(1, 25, Sort.NAME);
    var completableFuture = service.retrievePackages(VENDOR_ID, packageFilter, pageable);

    assertTrue(isCompletedNormally(completableFuture));
    var expectedPath = "/rm/rmaccounts/v2/" + STUB_CUSTOMER_ID + "/vendors/" + VENDOR_ID + "/lists";
    var expectedQuery = "?selection=all"
                        + "&contenttype=all"
                        + "&searchtype=advanced"
                        + "&searchfield=name"
                        + "&search="
                        + "&offset=1"
                        + "&count=25"
                        + "&orderby=packagename";
    wm.verify(getRequestedFor(urlEqualTo(expectedPath + expectedQuery)));
  }

  @Test
  void retrievePackage() throws Exception {
    var completableFuture = service.retrievePackage(PACKAGE_ID);

    assertTrue(isCompletedNormally(completableFuture));
    var expectedPackage = Json.decodeValue(getJson("package-2222.json"), PackageData.class);
    assertEquals(expectedPackage, completableFuture.join());
  }

  @Test
  void updatePackage() {
    var completableFuture = service.updatePackage(PACKAGE_ID, PackagePut.builder().build());

    assertTrue(isCompletedNormally(completableFuture));
    var urlPattern = urlEqualTo("/rm/rmaccounts/v2/" + STUB_CUSTOMER_ID + "/lists/" + PACKAGE_ID);
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern));
  }

  @Test
  void deletePackage() {
    var completableFuture = service.deletePackage(PACKAGE_ID);

    assertTrue(isCompletedNormally(completableFuture));
    var urlPattern = urlEqualTo("/rm/rmaccounts/v2/" + STUB_CUSTOMER_ID + "/lists/" + PACKAGE_ID);
    wm.verify(new RequestPatternBuilder(RequestMethod.PUT, urlPattern));
  }

  @Test
  void postPackage() {
    var completableFuture = service.postPackage(packagePost, VENDOR_ID);

    assertTrue(isCompletedNormally(completableFuture));
    var urlPatternPost = urlEqualTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/vendors/" + VENDOR_ID + "/packages");
    wm.verify(new RequestPatternBuilder(RequestMethod.POST, urlPatternPost));
    var urlPatternGet = urlEqualTo("/rm/rmaccounts/v2/" + STUB_CUSTOMER_ID + "/lists/" + PACKAGE_ID);
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPatternGet));
  }
}
