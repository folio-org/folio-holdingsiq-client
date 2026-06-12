package org.folio.holdingsiq.model;

public record ResourceId(
  long providerIdPart,
  long packageIdPart,
  long titleIdPart
) { }