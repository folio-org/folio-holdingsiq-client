package org.folio.holdingsiq.service;

import java.util.concurrent.CompletableFuture;

import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.Resource;
import org.folio.holdingsiq.model.ResourceFilter;
import org.folio.holdingsiq.model.ResourceId;
import org.folio.holdingsiq.model.ResourcePut;
import org.folio.holdingsiq.model.ResourceSelectedPayload;
import org.folio.holdingsiq.model.Resources;

public interface ResourcesHoldingsIQService {

  CompletableFuture<Resource> retrieveResource(ResourceId resourceId);

  CompletableFuture<Resource> retrieveResourceWithRelationships(ResourceId resourceId);

  CompletableFuture<Resources> retrieveResources(int packageId, ResourceFilter resourceFilter, Pageable pageable);

  CompletableFuture<Resource> postResource(ResourceSelectedPayload resourcePost, ResourceId resourceId);

  CompletableFuture<Void> updateResource(ResourceId parsedResourceId, ResourcePut resourcePutBody);

  CompletableFuture<Void> deleteResource(ResourceId parsedResourceId);
}
