package org.folio.holdingsiq.model;

import java.util.List;
import lombok.Builder;

/**
 * Title search filter.
 *
 * @param filterConsortia supported by the advanced (non list-scoped) title search only
 */
@Builder
public record TitleFilter(

  String query,
  SearchType searchType,
  TitleSearchField searchField,
  FilterSelected filterSelected,
  TitleFilterType filterType,
  FilterFreeAccess filterPackageFreeAccess,
  List<Integer> filterPackageIds,
  List<String> filterConsortia

) {}
