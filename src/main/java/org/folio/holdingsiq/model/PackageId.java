package org.folio.holdingsiq.model;

public record PackageId(
  long providerIdPart,
  long packageIdPart
) { }
