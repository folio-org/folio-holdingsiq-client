package org.folio.holdingsiq.service.impl;

import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.LISTS_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.PACKAGES_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.VENDORS_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.successBodyLogger;

import io.vertx.core.Vertx;
import java.util.concurrent.CompletableFuture;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.PackageCreated;
import org.folio.holdingsiq.model.PackageData;
import org.folio.holdingsiq.model.PackageFilter;
import org.folio.holdingsiq.model.PackagePost;
import org.folio.holdingsiq.model.PackagePut;
import org.folio.holdingsiq.model.PackageSelectedPayload;
import org.folio.holdingsiq.model.Packages;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.service.PackagesHoldingsIQService;
import org.folio.holdingsiq.service.impl.urlbuilder.PackagesFilterableUrlBuilder;

public class PackagesHoldingsIQServiceImpl implements PackagesHoldingsIQService {

  private final HoldingsRequestHelper holdingsRequestHelper;

  public PackagesHoldingsIQServiceImpl(Configuration config, Vertx vertx) {
    this.holdingsRequestHelper = new HoldingsRequestHelper(config, vertx).addBodyListener(successBodyLogger());
  }

  @Override
  public CompletableFuture<PackageData> retrievePackage(long packageId) {
    final String path = LISTS_PATH + '/' + packageId;
    return holdingsRequestHelper.getRequest(holdingsRequestHelper.constructURLv2(path), PackageData.class);
  }

  @Override
  public CompletableFuture<Packages> retrievePackages(PackageFilter packageFilter, Pageable pageable) {
    var queryParams = new PackagesFilterableUrlBuilder(packageFilter, pageable).build();
    var url = holdingsRequestHelper.constructURLv2(LISTS_PATH, queryParams);
    return holdingsRequestHelper.getRequest(url, Packages.class);
  }

  @Override
  public CompletableFuture<Packages> retrievePackages(long providerId, PackageFilter packageFilter, Pageable pageable) {
    var queryParams = new PackagesFilterableUrlBuilder(packageFilter, pageable).build();
    var url = holdingsRequestHelper.constructURLv2(VENDORS_PATH + '/' + providerId + '/' + LISTS_PATH, queryParams);
    return holdingsRequestHelper.getRequest(url, Packages.class);
  }

  @Override
  public CompletableFuture<PackageData> postPackage(PackagePost entity, long providerId) {
    String path = VENDORS_PATH + '/' + providerId + '/' + PACKAGES_PATH;
    return holdingsRequestHelper.postRequest(holdingsRequestHelper.constructURL(path), entity, PackageCreated.class)
      .thenCompose(packageCreated -> retrievePackage(packageCreated.packageId()));
  }

  @Override
  public CompletableFuture<Void> updatePackage(long packageId, PackagePut packagePut) {
    final String path = LISTS_PATH + '/' + packageId;
    return holdingsRequestHelper.putRequest(holdingsRequestHelper.constructURLv2(path), packagePut);
  }

  @Override
  public CompletableFuture<Void> deletePackage(long packageId) {
    final String path = LISTS_PATH + '/' + packageId;
    return holdingsRequestHelper.putRequest(holdingsRequestHelper.constructURLv2(path),
      new PackageSelectedPayload(false));
  }
}
