package org.folio.holdingsiq.service.impl;

import static io.vertx.core.http.HttpResponseExpectation.SC_ACCEPTED;
import static io.vertx.core.http.HttpResponseExpectation.SC_CONFLICT;
import static io.vertx.core.http.HttpResponseExpectation.SC_NO_CONTENT;
import static io.vertx.core.http.HttpResponseExpectation.SC_OK;
import static java.lang.String.format;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Expectation;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpResponseHead;
import io.vertx.core.http.PoolOptions;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.impl.ClientPhase;
import io.vertx.ext.web.client.impl.HttpContext;
import io.vertx.ext.web.client.impl.HttpRequestImpl;
import io.vertx.ext.web.client.impl.WebClientInternal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.log4j.Log4j2;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.service.exception.ResourceNotFoundException;
import org.folio.holdingsiq.service.exception.ResultsProcessingException;
import org.folio.holdingsiq.service.exception.ServiceResponseException;
import org.folio.holdingsiq.service.exception.UnAuthorizedException;

@Log4j2
class HoldingsRequestHelper {

  static final String VENDORS_PATH = "vendors";
  static final String PACKAGES_PATH = "packages";
  static final String TITLES_PATH = "titles";

  private static final String RMAPI_API_KEY_HEADER = "X-Api-Key";
  private static final String INVALID_RMAPI_RESPONSE = "Invalid RMAPI response";
  private static final String JSON_RESPONSE_ERROR = "Error processing RMAPI Response";
  private static final String VENDOR_LOWER_STRING = "vendor";
  private static final String PROVIDER_LOWER_STRING = "provider";
  private static final String VENDOR_UPPER_STRING = "Vendor";
  private static final String PROVIDER_UPPER_STRING = "Provider";
  private static final int HTTP_OK = 200;
  private static final int HTTP_CREATED = 201;
  private static final int HTTP_ACCEPTED = 202;
  private static final int HTTP_NO_CONTENT = 204;
  private static final int HTTP_UNAUTHORIZED = 401;
  private static final int HTTP_FORBIDDEN = 403;
  private static final int HTTP_NOT_FOUND = 404;

  private final String customerId;
  private final String apiKey;
  private final String baseURI;

  private final Vertx vertx;
  private final List<HoldingsResponseBodyListener> bodyListeners;

  HoldingsRequestHelper(Configuration config, Vertx vertx) {
    this.customerId = config.getCustomerId();
    this.apiKey = config.getApiKey();
    this.baseURI = config.getUrl();
    this.vertx = vertx;
    this.bodyListeners = new ArrayList<>();
  }

  <T> CompletableFuture<T> getRequest(String query, Class<T> clazz) {
    var request = createGetRequest(query);
    CompletableFuture<T> result = new CompletableFuture<>();

    request.send()
      .onComplete((res, failure) ->
        handleResponse(res, failure, request, query, result, SC_OK, clazz));

    return result;
  }

  <T> CompletableFuture<Void> putRequest(String query, T putData) {
    var request = createPutRequest(query);
    CompletableFuture<Void> result = new CompletableFuture<>();

    request.sendJson(putData)
      .onComplete((res, failure) ->
        handleResponse(res, failure, request, query, result, SC_NO_CONTENT, null));

    return result;
  }

  <T, P> CompletableFuture<T> postRequest(String query, P postData, Class<T> clazz) {
    var request = createPostRequest(query);
    CompletableFuture<T> result = new CompletableFuture<>();

    request.sendJson(postData)
      .onComplete((res, failure) ->
        handleResponse(res, failure, request, query, result, SC_OK.or(SC_ACCEPTED), clazz));

    return result;
  }

  <T> CompletableFuture<T> postRequest(String query, Class<T> clazz) {
    var request = createPostRequest(query);
    CompletableFuture<T> result = new CompletableFuture<>();

    request.send()
      .onComplete((res, failure) ->
        handleResponse(res, failure, request, query, result, SC_ACCEPTED.or(SC_CONFLICT), clazz));

    return result;
  }

  HoldingsRequestHelper addBodyListener(HoldingsResponseBodyListener listener) {
    if (listener != null) {
      bodyListeners.add(listener);
    }
    return this;
  }

  String constructURL(String path) {
    String fullPath = format("%s/rm/rmaccounts/%s/%s", baseURI, customerId, path);
    log.debug("constructURL - path={}", fullPath);
    return fullPath;
  }

  static HoldingsResponseBodyListener successBodyLogger() {
    return (body, ctx) -> {
      int statusCode = ctx.statusCode();
      if (isSuccessStatusCode(statusCode)) {
        log.debug("[OK] RMAPI Service response: query = [{}], statusCode = [{}]", ctx.uri(), statusCode);
      }
    };
  }

  private HttpRequest<Buffer> createGetRequest(String query) {
    var client = WebClientHolder.getClient(vertx);
    return addHeaders(client.getAbs(query));
  }

  private HttpRequest<Buffer> createPutRequest(String query) {
    var client = WebClientHolder.getClient(vertx);
    return addHeaders(client.putAbs(query));
  }

  private HttpRequest<Buffer> createPostRequest(String query) {
    var client = WebClientHolder.getClient(vertx);
    return addHeaders(client.postAbs(query));
  }

  private static boolean isSuccessStatusCode(int statusCode) {
    return statusCode == HTTP_OK || statusCode == HTTP_CREATED
           || statusCode == HTTP_ACCEPTED || statusCode == HTTP_NO_CONTENT;
  }

  private <T> void handleResponse(HttpResponse<Buffer> response, Throwable failure,
                                  HttpRequest<?> request, String query, CompletableFuture<T> result,
                                  Expectation<HttpResponseHead> expectation, Class<T> clazz) {
    if (failure != null || response == null || !expectation.test(response)) {
      handleErrorResponse(response, failure, query, result);
      return;
    }

    try {
      var body = response.body();
      fireBodyReceived(body, new HoldingsInteractionContext(request, response));
      T decodedBody = decodeResponseBody(body, clazz);
      result.complete(decodedBody);
    } catch (Exception e) {
      log.error("Error processing response body for query = [{}]: {}", query, e.getMessage());
      result.completeExceptionally(e);
    }
  }

  private <T> T decodeResponseBody(Buffer body, Class<T> clazz) {
    if (body == null) {
      return null;
    }
    if (clazz == String.class) {
      @SuppressWarnings("unchecked")
      var bodyString = (T) body.toString();
      return bodyString;
    }
    try {
      return Json.decodeValue(body, clazz);
    } catch (DecodeException e) {
      log.error("{} - Response = [{}] Target Type = [{}] Cause: [{}]", JSON_RESPONSE_ERROR, body.toString(), clazz,
        e.getMessage());
      throw new ResultsProcessingException(JSON_RESPONSE_ERROR, e);
    }
  }

  private <T> void handleErrorResponse(HttpResponse<Buffer> res, Throwable failure,
                                       String query, CompletableFuture<T> result) {
    if (res != null) {
      handleHttpFailure(res, query, result);
    } else {
      log.error("Request failed for query = [{}]: {}", query, failure.getMessage());
      result.completeExceptionally(failure);
    }
  }

  private <T> void handleHttpFailure(HttpResponse<Buffer> res, String query, CompletableFuture<T> result) {
    var body = res.body() == null ? "" : res.body().toString();
    var statusCode = res.statusCode();
    var statusMessage = res.statusMessage();

    log.error("{} status code = [{}] status message = [{}] query = [{}] body = [{}]",
      INVALID_RMAPI_RESPONSE, statusCode, statusMessage, query, body);

    String mappedBody = mapVendorToProvider(body);
    result.completeExceptionally(createException(statusCode, statusMessage, mappedBody, query));
  }

  private Exception createException(int statusCode, String statusMessage, String body, String query) {
    return switch (statusCode) {
      case HTTP_NOT_FOUND -> new ResourceNotFoundException(
        format("Requested resource %s not found", query), statusCode, statusMessage, body, query);
      case HTTP_UNAUTHORIZED, HTTP_FORBIDDEN -> new UnAuthorizedException(
        format("Unauthorized Access to %s", query), statusCode, statusMessage, body, query);
      default -> new ServiceResponseException(
        format("%s Code = %s Message = %s Body = %s", INVALID_RMAPI_RESPONSE, statusCode, statusMessage, body),
        statusCode, statusMessage, body, query);
    };
  }

  private String mapVendorToProvider(String msgBody) {
    return msgBody.replace(VENDOR_LOWER_STRING, PROVIDER_LOWER_STRING)
      .replace(VENDOR_UPPER_STRING, PROVIDER_UPPER_STRING);
  }

  private <T> void fireBodyReceived(T body, HoldingsInteractionContext context) {
    for (HoldingsResponseBodyListener listener : bodyListeners) {
      listener.bodyReceived(body, context);
    }
  }

  private HttpRequest<Buffer> addHeaders(HttpRequest<Buffer> request) {
    return request
      .putHeader(HttpHeaders.ACCEPT.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
      .putHeader(HttpHeaders.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
      .putHeader(RMAPI_API_KEY_HEADER, apiKey);
  }

  private static class WebClientHolder {

    private static final Map<Vertx, WebClientHolder> webClients = new ConcurrentHashMap<>();
    private static final int HTTP1_MAX_CONNECTIONS = 20;
    private static final int HTTP2_MAX_CONNECTIONS = 3;

    private final WebClient webClient;

    WebClientHolder(WebClient wc) {
      this.webClient = wc;
      ((WebClientInternal) webClient).addInterceptor(createLoggingInterceptor());
    }

    static WebClient getClient(Vertx vertx) {
      return webClients.computeIfAbsent(vertx, vtx ->
        new WebClientHolder(createWebClient(vtx))
      ).getWebClient();
    }

    WebClient getWebClient() {
      return webClient;
    }

    private static WebClient createWebClient(Vertx vtx) {
      PoolOptions poolOptions = new PoolOptions()
        .setHttp1MaxSize(HTTP1_MAX_CONNECTIONS)
        .setHttp2MaxSize(HTTP2_MAX_CONNECTIONS);

      var webClient = WebClient.create(vtx, new WebClientOptions(), poolOptions);
      log.info("Web client instance created to serve requests to HoldingsIQ");

      return webClient;
    }

    private Handler<HttpContext<?>> createLoggingInterceptor() {
      return httpContext -> {
        if (ClientPhase.SEND_REQUEST == httpContext.phase()) {
          logRequest(httpContext);
        }
        httpContext.next();
      };
    }

    private void logRequest(HttpContext<?> httpContext) {
      HttpRequestImpl<?> request = (HttpRequestImpl<?>) httpContext.request();
      String uri = request.uri();
      log.debug("RMAPI Service absolute URL is: {}", uri);

      Object requestBody = httpContext.body();
      if (requestBody != null) {
        log.debug("RMAPI Service body is: {}", requestBody);
      }
    }
  }
}
