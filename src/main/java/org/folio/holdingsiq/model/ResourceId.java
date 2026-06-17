package org.folio.holdingsiq.model;

public record ResourceId(
  int providerIdPart,
  int packageIdPart,
  int titleIdPart
) { }