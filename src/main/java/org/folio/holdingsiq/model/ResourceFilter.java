package org.folio.holdingsiq.model;

import lombok.Builder;

/**
 * Filter for searching resources (titles) within a single list (package).
 */
@Builder
public record ResourceFilter(

  String query,
  TitleSearchField searchField,
  FilterSelected filterSelected,
  TitleFilterType filterType,
  FilterFreeAccess filterPackageFreeAccess

) {}
