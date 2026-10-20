package org.folio.holdingsiq.model;

import java.util.List;
import lombok.Builder;

@Builder
public record PackageFilter(

  String query,
  SearchType searchType,
  PackageFilterType filterType,
  PackageSearchField searchField,
  FilterSelected filterSelected,
  PackageFilterVisibility filterVisibility,
  FilterFreeAccess filterPackageFreeAccess,
  Boolean filterPrimaryPackage,
  String highlightTag,
  Boolean includeSubjectFacet,
  Integer subjectFacetCount,
  List<Integer> filterSubjectIds,
  List<String> filterSubjectSchemas

) {}
