package org.folio.holdingsiq.model;

public record PackageId(
  int providerIdPart,
  int packageIdPart
) { }
