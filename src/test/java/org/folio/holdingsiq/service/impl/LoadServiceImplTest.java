package org.folio.holdingsiq.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.folio.holdingsiq.model.TransactionId;
import org.folio.holdingsiq.service.LoadService;

class LoadServiceImplTest extends HoldingsIQServiceTestConfig {

  private static final String PREVIOUS_TRANSACTION_ID = "abcd3ab0-da4b-4a1f-a004-a9d323e54cde";
  private static final String TRANSACTION_ID = "84113ab0-da4b-4a1f-a004-a9d686e54811";

  private static final String DELTA_ID = "7e3537a0-3f30-4ef8-9470-dd0a87ac1066";

  private LoadService service;

  @BeforeEach
  void setUp() {
    service = new LoadServiceImpl(getConfiguration(), Vertx.vertx());
  }

  @Test
  void postHoldings() {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/holdings"), false);
    wm.stubFor(
      post(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_ACCEPTED).withBody("{}"))
    );
    var completableFuture = service.populateHoldings();

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.POST, urlPattern));
  }

  @Test
  void postHoldingsForce() {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/holdings?force=true"), false);
    wm.stubFor(
      post(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_ACCEPTED).withBody("{}"))
    );
    var completableFuture = service.populateHoldingsForce();

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.POST, urlPattern));
  }

  @Test
  void postHoldingsTransaction() throws Exception {
    TransactionId response = TransactionId.builder().transactionId(TRANSACTION_ID).build();
    var urlPattern =
      new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/reports/holdings?format=kbart2"), false);
    wm.stubFor(
      post(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_ACCEPTED).withBody(Json.encode(response)))
    );
    var completableFuture = service.populateHoldingsTransaction();

    assertTrue(isCompletedNormally(completableFuture));
    assertEquals(TRANSACTION_ID, completableFuture.get().getTransactionId());
    wm.verify(new RequestPatternBuilder(RequestMethod.POST, urlPattern));
  }

  @Test
  void getStatusHoldings() {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/holdings/status"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.getLoadingStatus();

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void getTransactionStatus() {
    var urlPattern = new UrlPattern(
      equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/reports/holdings/transactions/" + TRANSACTION_ID + "/status"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.getTransactionStatus(TRANSACTION_ID);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void getTransactions() {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/reports/holdings/transactions"), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.getTransactions();

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void getHoldings() {
    var urlPattern = new UrlPattern(equalTo(
      "/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/holdings?format=kbart2&count=" + COUNT_FOR_PARAM + "&offset="
        + PAGE_FOR_PARAM), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.loadHoldings(COUNT_FOR_PARAM, PAGE_FOR_PARAM);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void getHoldingsTransaction() {
    var urlPattern = new UrlPattern(equalTo(
      "/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/reports/holdings/transactions/" + TRANSACTION_ID + "?format=kbart2&count="
        + COUNT_FOR_PARAM + "&offset=" + PAGE_FOR_PARAM), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.loadHoldingsTransaction(TRANSACTION_ID, COUNT_FOR_PARAM, PAGE_FOR_PARAM);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void postDeltaReport() throws Exception {
    var urlPattern = new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/reports/holdings/deltas"), false);
    wm.stubFor(
      post(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_ACCEPTED).withBody(DELTA_ID))
    );
    var completableFuture = service.populateDeltaReport(TRANSACTION_ID, PREVIOUS_TRANSACTION_ID);

    assertTrue(isCompletedNormally(completableFuture));
    assertEquals(DELTA_ID, completableFuture.get());
    wm.verify(new RequestPatternBuilder(RequestMethod.POST, urlPattern));
  }

  @Test
  void getDeltaReport() {
    var urlPattern = new UrlPattern(equalTo(
      "/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/reports/holdings/deltas/" + DELTA_ID + "?format=kbart2&count="
        + COUNT_FOR_PARAM + "&offset=" + PAGE_FOR_PARAM), false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.loadDeltaReport(DELTA_ID, COUNT_FOR_PARAM, PAGE_FOR_PARAM);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }

  @Test
  void getDeltaReportStatus() {
    var urlPattern =
      new UrlPattern(equalTo("/rm/rmaccounts/" + STUB_CUSTOMER_ID + "/reports/holdings/deltas/" + DELTA_ID + "/status"),
        false);
    wm.stubFor(
      get(urlPattern).willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{}"))
    );
    var completableFuture = service.getDeltaReportStatus(DELTA_ID);

    assertTrue(isCompletedNormally(completableFuture));
    wm.verify(new RequestPatternBuilder(RequestMethod.GET, urlPattern));
  }
}
