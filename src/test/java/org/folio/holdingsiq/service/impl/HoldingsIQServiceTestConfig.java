package org.folio.holdingsiq.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.Json;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.FilterQuery;
import org.folio.holdingsiq.model.PackageCreated;
import org.folio.holdingsiq.model.PackageId;
import org.folio.holdingsiq.model.PackagePost;
import org.folio.holdingsiq.model.ResourceId;
import org.folio.holdingsiq.model.ResourcePut;
import org.folio.holdingsiq.model.RootProxyCustomLabels;
import org.folio.holdingsiq.model.TitleCreated;
import org.folio.holdingsiq.model.TitlePost;
import org.folio.holdingsiq.model.Titles;
import org.folio.holdingsiq.model.VendorPut;

public class HoldingsIQServiceTestConfig {

  protected static final String STUB_CUSTOMER_ID = "TEST_CUSTOMER_ID";
  protected static final String STUB_API_KEY = "test_key";
  protected static final String STUB_BASE_URL = "https://sandbox.ebsco.io";
  protected static final String DUMMY_RESPONSE_BODY = "{}";
  protected static final int PAGE_FOR_PARAM = 1;
  protected static final int COUNT_FOR_PARAM = 5;
  protected static final Long PACKAGE_ID = 2222L;
  protected static final Long TITLE_ID = 3333L;
  protected static final Long VENDOR_ID = 5555L;
  protected static final Configuration CONFIGURATION =
    Configuration.builder().customerId(STUB_CUSTOMER_ID).apiKey(STUB_API_KEY).url(STUB_BASE_URL).build();

  protected Vertx mockVertx = mock(Vertx.class);
  protected HttpClient mockClient = mock(HttpClient.class);
  protected HttpClientRequest mockRequest = mock(HttpClientRequest.class);
  protected HttpClientResponse mockResponse = mock(HttpClientResponse.class);
  protected Buffer mockResponseBody = mock(Buffer.class);
  protected MultiMap stubHeaderMap = MultiMap.caseInsensitiveMultiMap();
  protected ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);

  protected ObjectMapper savedPrettyMapper;
  protected ObjectMapper savedMapper;
  protected FilterQuery filterQuery = FilterQuery.builder().build();
  protected VendorPut vendorPut = VendorPut.builder().build();
  protected ResourcePut resourcePut = ResourcePut.builder().build();
  protected PackagePost packagePost = PackagePost.builder().build();
  protected TitlePost titlePost = TitlePost.builder().build();

  protected TitleCreated titleCreated = TitleCreated.builder().titleId(TITLE_ID).build();
  protected PackageCreated packageCreated = PackageCreated.builder().packageId(PACKAGE_ID).build();
  protected Titles titles = Titles.builder().titleList(Collections.emptyList()).build();
  protected RootProxyCustomLabels rootProxyCustomLabels =
    RootProxyCustomLabels.builder().vendorId(String.valueOf(VENDOR_ID)).build();
  protected PackageId packageId = PackageId.builder().providerIdPart(VENDOR_ID).packageIdPart(PACKAGE_ID).build();
  protected ResourceId resourceId = ResourceId.builder().providerIdPart(VENDOR_ID)
    .packageIdPart(PACKAGE_ID).titleIdPart(TITLE_ID).build();

  protected void setUpStep() throws IOException {
    ArgumentCaptor<Handler<HttpClientResponse>> requestHandler = ArgumentCaptor.forClass(Handler.class);
    ArgumentCaptor<Handler<Throwable>> exceptionHandler = ArgumentCaptor.forClass(Handler.class);

    when(mockVertx.createHttpClient()).thenReturn(mockClient);
    when(mockClient.getAbs(url.capture())).thenReturn(mockRequest);
    when(mockClient.putAbs(url.capture())).thenReturn(mockRequest);
    when(mockClient.postAbs(url.capture())).thenReturn(mockRequest);
    when(mockRequest.headers()).thenReturn(stubHeaderMap);
    when(mockRequest.handler(requestHandler.capture())).thenReturn(mockRequest);
    when(mockRequest.exceptionHandler(exceptionHandler.capture())).thenReturn(mockRequest);
    when(mockResponse.bodyHandler(any())).thenAnswer(callHandlerWithBody());
    doAnswer(callHandlerWithResponse(requestHandler)).when(mockRequest).end();
    doAnswer(callHandlerWithResponse(requestHandler)).when(mockRequest).end(anyString());

    //Save mappers to restore them later
    savedMapper = Json.mapper;
    savedPrettyMapper = Json.prettyMapper;
    Json.prettyMapper = spy(Json.prettyMapper);
    Json.mapper = spy(Json.mapper);
    when(Json.prettyMapper.writeValueAsString(any())).thenReturn(DUMMY_RESPONSE_BODY);

    JsonParser jsonParser = mock(JsonParser.class);
    when(jsonParser.nextToken()).thenReturn(null);
    JsonFactory jsonFactory = mock(JsonFactory.class);
    when(jsonFactory.createParser(any(String.class))).thenReturn(jsonParser);
    when(Json.mapper.getFactory()).thenReturn(jsonFactory);
    doReturn(null).when(Json.mapper).readValue(any(JsonParser.class), any(Class.class));
  }

  protected void tearDownStep() {
    Json.mapper = savedMapper;
    Json.prettyMapper = savedPrettyMapper;
  }

  protected boolean isCompletedNormally(CompletableFuture<?> completableFuture) {
    return completableFuture.isDone() && !completableFuture.isCompletedExceptionally() && !completableFuture.isCancelled();
  }

  private Answer<Object> callHandlerWithBody() {
    return invocation -> {
      Handler<Buffer> handler = invocation.getArgument(0);
      handler.handle(mockResponseBody);
      return mockResponse;
    };
  }

  private Answer<?> callHandlerWithResponse(ArgumentCaptor<Handler<HttpClientResponse>> requestHandler) {
    return invocation -> {
      requestHandler.getValue().handle(mockResponse);
      return null;
    };
  }
}
