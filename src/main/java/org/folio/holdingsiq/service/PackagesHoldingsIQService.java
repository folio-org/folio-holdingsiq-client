package org.folio.holdingsiq.service;

import java.util.concurrent.CompletableFuture;
import org.folio.holdingsiq.model.PackageData;
import org.folio.holdingsiq.model.PackageFilter;
import org.folio.holdingsiq.model.PackagePost;
import org.folio.holdingsiq.model.PackagePut;
import org.folio.holdingsiq.model.Packages;
import org.folio.holdingsiq.model.Pageable;

public interface PackagesHoldingsIQService {

  CompletableFuture<PackageData> retrievePackage(long packageId);

  CompletableFuture<Packages> retrievePackages(PackageFilter packageFilter, Pageable pageable);

  CompletableFuture<Packages> retrievePackages(long providerId, PackageFilter packageFilter, Pageable pageable);

  CompletableFuture<PackageData> postPackage(PackagePost entity, long providerId);

  CompletableFuture<Void> updatePackage(long packageId, PackagePut packagePut);

  CompletableFuture<Void> deletePackage(long packageId);
}
