package org.folio.holdingsiq.model;

import java.util.List;
import lombok.Builder;

@Builder
public record PackageFilter(

  String query,
  SearchType searchType,
  PackageFilterType filterType,
  PackageSearchField searchField,
  PackageFilterSelected filterSelected,
  PackageFilterVisibility filterVisibility,
  PackageFilterFreeAccess filterPackageFreeAccess,
  Boolean filterPrimaryPackage,
  String highlightTag,
  Boolean includeSubjectFacet,
  Integer subjectFacetCount,
  List<Integer> filterSubjectIds

) {}
