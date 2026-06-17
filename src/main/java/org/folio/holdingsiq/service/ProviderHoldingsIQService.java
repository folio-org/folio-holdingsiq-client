package org.folio.holdingsiq.service;

import java.util.concurrent.CompletableFuture;

import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.model.VendorById;
import org.folio.holdingsiq.model.VendorPut;
import org.folio.holdingsiq.model.Vendors;

public interface ProviderHoldingsIQService {

  CompletableFuture<Integer> getVendorId();
  CompletableFuture<VendorById> retrieveProvider(int id);
  CompletableFuture<Vendors> retrieveProviders(String q, int page, int count, Sort sort);
  CompletableFuture<VendorById> updateProvider(int id, VendorPut vendorPut);

}
