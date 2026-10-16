package org.folio.holdingsiq.service.impl;

import static java.lang.String.format;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.successBodyLogger;

import io.vertx.core.Vertx;
import java.util.concurrent.CompletableFuture;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.Resource;
import org.folio.holdingsiq.model.ResourceDeletePayload;
import org.folio.holdingsiq.model.ResourceFilter;
import org.folio.holdingsiq.model.ResourceId;
import org.folio.holdingsiq.model.ResourcePut;
import org.folio.holdingsiq.model.ResourceSelectedPayload;
import org.folio.holdingsiq.model.Resources;
import org.folio.holdingsiq.service.ResourcesHoldingsIQService;
import org.folio.holdingsiq.service.impl.urlbuilder.ResourcesFilterableUrlBuilder;

public class ResourcesHoldingsIQServiceImpl implements ResourcesHoldingsIQService {

  private static final String RESOURCES_ENDPOINT_FORMAT = "lists/%s/titles";
  private static final String RESOURCE_ENDPOINT_FORMAT = RESOURCES_ENDPOINT_FORMAT + "/%s";
  private static final String INCLUDE_RELATIONSHIPS_PARAM = "includerelationships=true";
  private final HoldingsRequestHelper holdingsRequestHelper;

  public ResourcesHoldingsIQServiceImpl(Configuration config, Vertx vertx) {
    holdingsRequestHelper = new HoldingsRequestHelper(config, vertx);
    holdingsRequestHelper.addBodyListener(successBodyLogger());
  }

  @Override
  public CompletableFuture<Resource> postResource(ResourceSelectedPayload resourcePost, ResourceId resourceId) {
    var requestUrl = holdingsRequestHelper.constructURLv2(resourcePath(resourceId));
    return holdingsRequestHelper.putRequest(requestUrl, resourcePost)
      .thenCompose(o -> this.retrieveResource(resourceId));
  }

  @Override
  public CompletableFuture<Void> updateResource(ResourceId parsedResourceId, ResourcePut resourcePutBody) {
    var requestUrl = holdingsRequestHelper.constructURLv2(resourcePath(parsedResourceId));
    return holdingsRequestHelper.putRequest(requestUrl, resourcePutBody);
  }

  @Override
  public CompletableFuture<Void> deleteResource(ResourceId parsedResourceId) {
    var requestUrl = holdingsRequestHelper.constructURLv2(resourcePath(parsedResourceId));
    return holdingsRequestHelper.putRequest(requestUrl, new ResourceDeletePayload(false));
  }

  @Override
  public CompletableFuture<Resource> retrieveResource(ResourceId resourceId) {
    return getResource(resourceId, false);
  }

  @Override
  public CompletableFuture<Resource> retrieveResourceWithRelationships(ResourceId resourceId) {
    return getResource(resourceId, true);
  }

  @Override
  public CompletableFuture<Resources> retrieveResources(int packageId, ResourceFilter resourceFilter,
                                                        Pageable pageable) {
    var queryParams = new ResourcesFilterableUrlBuilder(resourceFilter, pageable).build();
    var requestUrl = holdingsRequestHelper.constructURLv2(format(RESOURCES_ENDPOINT_FORMAT, packageId), queryParams);
    return holdingsRequestHelper.getRequest(requestUrl, Resources.class);
  }

  private CompletableFuture<Resource> getResource(ResourceId resourceId, boolean includeRelationships) {
    var path = resourcePath(resourceId);
    var requestUrl = includeRelationships
                     ? holdingsRequestHelper.constructURLv2(path, INCLUDE_RELATIONSHIPS_PARAM)
                     : holdingsRequestHelper.constructURLv2(path);
    return holdingsRequestHelper.getRequest(requestUrl, Resource.class);
  }

  private String resourcePath(ResourceId resourceId) {
    return format(RESOURCE_ENDPOINT_FORMAT, resourceId.packageIdPart(), resourceId.titleIdPart());
  }
}
