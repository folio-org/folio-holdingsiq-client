package org.folio.holdingsiq.service.impl;

import static java.lang.String.format;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.impl.ClientPhase;
import io.vertx.ext.web.client.impl.HttpContext;
import io.vertx.ext.web.client.impl.HttpRequestImpl;
import io.vertx.ext.web.client.impl.WebClientInternal;
import io.vertx.ext.web.client.predicate.ErrorConverter;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.client.predicate.ResponsePredicateResult;
import io.vertx.ext.web.codec.BodyCodec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
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
  private static final String JSON_RESPONSE_ERROR = "Error processing RMAPI Response";
  private static final String INVALID_RMAPI_RESPONSE = "Invalid RMAPI response";
  private static final String VENDOR_LOWER_STRING = "vendor";
  private static final String PROVIDER_LOWER_STRING = "provider";
  private static final String VENDOR_UPPER_STRING = "Vendor";
  private static final String PROVIDER_UPPER_STRING = "Provider";

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
    var client = WebClientHolder.getClient(vertx);

    var request = addHeaders(client.getAbs(query))
        .expect(ResponsePredicate.create(ResponsePredicate.SC_OK, errorConverter(query)))
        .as(getResponseCodec(clazz));

    return sendHttpRequest(request, HttpRequest::send);
  }

  <T> CompletableFuture<Void> putRequest(String query, T putData) {
    var client = WebClientHolder.getClient(vertx);

    var request = addHeaders(client.putAbs(query))
      .expect(ResponsePredicate.create(ResponsePredicate.SC_NO_CONTENT, errorConverter(query)))
      .as(BodyCodec.none());

    return sendHttpRequest(request, req -> req.sendJson(putData));
  }

  <T, P> CompletableFuture<T> postRequest(String query, P postData, Class<T> clazz) {
    var client = WebClientHolder.getClient(vertx);

    var request = addHeaders(client.postAbs(query))
      .expect(ResponsePredicate.create(expectedStatusesPredicate(200, 202), errorConverter(query)))
      .as(getResponseCodec(clazz));

    return sendHttpRequest(request, req -> req.sendJson(postData));
  }

  <T> CompletableFuture<T> postRequest(String query, Class<T> clazz) {
    var client = WebClientHolder.getClient(vertx);

    var request = addHeaders(client.postAbs(query))
      .expect(ResponsePredicate.create(expectedStatusesPredicate(202, 409), errorConverter(query)))
      .as(getResponseCodec(clazz));

    return sendHttpRequest(request, HttpRequest::send);
  }

  HoldingsRequestHelper addBodyListener(HoldingsResponseBodyListener listener) {
    if (listener != null) {
      bodyListeners.add(listener);
    }
    return this;
  }

  String constructURL(String path) {
    String fullPath = format("%s/rm/rmaccounts/%s/%s", baseURI, customerId, path);

    log.info("constructurl - path=" + fullPath);
    return fullPath;
  }

  static HoldingsResponseBodyListener successBodyLogger() {
    return (body, ctx) -> {
      int sc = ctx.statusCode();

      if (sc == 200 || sc == 201 || sc == 202 || sc == 204) {
        log.info("[OK] RMAPI Service response: query = [{}], statusCode = [{}], body = [{}]", ctx.uri(), sc, body);
      }
    };
  }

  private <T> BodyCodec<T> getResponseCodec(Class<T> clazz) {
    return BodyCodec.create(buffer -> {
      if (clazz == String.class) {
        @SuppressWarnings("unchecked")
        var t = (T) buffer.toString();
        return t;
      } else {
        try {
          return Json.decodeValue(buffer, clazz);
        } catch (Exception e) {
          log.error("{} - Response = [{}] Target Type = [{}] Cause: [{}]", JSON_RESPONSE_ERROR, buffer.toString(), clazz,
            e.getMessage());
          throw new ResultsProcessingException(JSON_RESPONSE_ERROR, e);
        }
      }
    });
  }

  private ErrorConverter errorConverter(String query) {
    return ErrorConverter.createFullBody(result -> {
      HttpResponse<Buffer> response = result.response();
      var body = response.body().toString();
      var statusCode = response.statusCode();
      var statusMessage = response.statusMessage();
      log.error("{} status code = [{}] status message = [{}] query = [{}] body = [{}]",
        INVALID_RMAPI_RESPONSE, statusCode, statusMessage, query, body);

      String msgBody = mapVendorToProvider(body);

      if (statusCode == 404) {
        var message = format("Requested resource %s not found", query);
        return new ResourceNotFoundException(message, statusCode, statusMessage, msgBody, query);
      } else if ((statusCode == 401) || (statusCode == 403)) {
        var message = format("Unauthorized Access to %s", query);
        return new UnAuthorizedException(message, statusCode, statusMessage, msgBody, query);
      } else {
        var message = format("%s Code = %s Message = %s Body = %s", INVALID_RMAPI_RESPONSE, statusCode, statusMessage, body);
        return new ServiceResponseException(message, statusCode, statusMessage, msgBody, query);
      }
    });
  }

  private String mapVendorToProvider(String msgBody) {
    return msgBody.replace(VENDOR_LOWER_STRING, PROVIDER_LOWER_STRING).replace(VENDOR_UPPER_STRING, PROVIDER_UPPER_STRING);
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

  private <T> CompletableFuture<T> sendHttpRequest(HttpRequest<T> request,
      Function<HttpRequest<T>, Future<HttpResponse<T>>> sendMethod) {

    CompletableFuture<T> result = new CompletableFuture<>();

    Future<HttpResponse<T>> response = sendMethod.apply(request);

    response
        .onSuccess(res -> {
          T body = res.body();
          fireBodyReceived(body, new HoldingsInteractionContext(request, res));

          result.complete(res.body());
        })
        .onFailure(result::completeExceptionally);

    return result;
  }

  private ResponsePredicate expectedStatusesPredicate(int... expectedStatuses) {
    return response -> {
      var sc = response.statusCode();
      return ArrayUtils.contains(expectedStatuses, sc)
        ? ResponsePredicateResult.success()
        : ResponsePredicateResult.failure("Response status code " + sc + " is not in " + Arrays.toString(expectedStatuses));
    };
  }

  private static class WebClientHolder {

    // in most cases there will be only one key-value pair in the map: one vert.x with one web client associated
    private static final Map<Vertx, WebClientHolder> webClients = new ConcurrentHashMap<>();

    private final WebClient webClient;


    WebClientHolder(WebClient wc) {
      this.webClient = wc;
      ((WebClientInternal) webClient).addInterceptor(loggingInterceptor());
    }

    static WebClient getClient(Vertx vertx) {
      return webClients.computeIfAbsent(vertx, vtx -> {
        WebClient webClient = createWebClient(vtx);

        return new WebClientHolder(webClient);
      }).getWebClient();
    }

    private static WebClient createWebClient(Vertx vtx) {
      WebClientOptions options = new WebClientOptions();
      options.setMaxPoolSize(20); // override the default of 5 HTTP/1 connections
      options.setHttp2MaxPoolSize(3); // override the default of 1 HTTP/2 connections

      var webClient = WebClient.create(vtx, options);
      log.info("Web client instance created to serve requests to HoldingsIQ: {}", webClient);

      return webClient;
    }

    WebClient getWebClient() {
      return webClient;
    }

    private Handler<HttpContext<?>> loggingInterceptor() {
      return httpContext -> {
        if (ClientPhase.SEND_REQUEST == httpContext.phase()) {
          HttpRequestImpl<?> request = (HttpRequestImpl<?>) httpContext.request();

          String uri = request.uri();

          log.info("RMAPI Service absolute URL is: {}", uri);

          Object requestBody = httpContext.body();
          if (requestBody != null) {
            log.info("RMAPI Service body is: {}", requestBody);
          }
        }

        httpContext.next();
      };
    }
  }

}
