package org.folio.holdingsiq.service.impl.urlbuilder;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.requireNonNullElse;

import java.util.ArrayList;
import java.util.List;
import org.folio.holdingsiq.model.PackageFilter;
import org.folio.holdingsiq.model.PackageFilterFreeAccess;
import org.folio.holdingsiq.model.PackageFilterSelected;
import org.folio.holdingsiq.model.PackageFilterType;
import org.folio.holdingsiq.model.PackageFilterVisibility;
import org.folio.holdingsiq.model.PackageSearchField;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.SearchType;
import org.folio.holdingsiq.model.Sort;

public class PackagesFilterableUrlBuilder {

  private static final String SUBJECT_FACET = "subject";
  private static final int SUBJECT_FACET_COUNT_DEFAULT = 1000;

  private final PackageFilterFreeAccess filterPackageFreeAccess;
  private final boolean filterPrimaryPackage;
  private final PackageFilterVisibility filterVisibility;
  private final PackageFilterSelected filterSelected;
  private final PackageFilterType filterType;
  private final SearchType searchType;
  private final PackageSearchField searchField;
  private final String highlightTag;
  private final boolean includeSubjectFacet;
  private final int subjectFacetCount;
  private final int page;
  private final int count;
  private final Sort sort;
  private final String q;

  public PackagesFilterableUrlBuilder(PackageFilter packageFilter, Pageable pageable) {
    this.filterPackageFreeAccess = packageFilter.filterPackageFreeAccess();
    this.filterPrimaryPackage = TRUE.equals(packageFilter.filterPrimaryPackage());
    this.filterVisibility = packageFilter.filterVisibility();
    this.filterSelected = requireNonNullElse(packageFilter.filterSelected(), PackageFilterSelected.ALL);
    this.filterType = requireNonNullElse(packageFilter.filterType(), PackageFilterType.ALL);
    this.searchType = requireNonNullElse(packageFilter.searchType(), SearchType.ADVANCED);
    this.searchField = requireNonNullElse(packageFilter.searchField(), PackageSearchField.NAME);
    this.highlightTag = packageFilter.highlightTag();
    this.includeSubjectFacet = TRUE.equals(packageFilter.includeSubjectFacet());
    this.subjectFacetCount = packageFilter.subjectFacetCount() != null
                             ? packageFilter.subjectFacetCount()
                             : SUBJECT_FACET_COUNT_DEFAULT;
    this.q = packageFilter.query();
    this.page = pageable.page();
    this.count = pageable.count();
    this.sort = pageable.sort();
  }

  public String build() {
    String query = new QueryableUrlBuilder()
      .q(q)
      .page(page)
      .count(count)
      .sort(sort)
      .nameParameter("packagename")
      .build();

    List<String> parameters = new ArrayList<>();
    parameters.add("selection=" + filterSelected.getValue());
    parameters.add("contenttype=" + filterType.getValue());
    parameters.add("searchtype=" + searchType.getValue());
    parameters.add("searchfield=" + searchField.getValue());
    if (filterVisibility != null) {
      parameters.add("visibilitytype=" + filterVisibility.getValue());
    }
    if (highlightTag != null) {
      parameters.add("highlighttag=" + highlightTag);
    }
    if (includeSubjectFacet) {
      parameters.add("facetstoinclude=" + SUBJECT_FACET);
      parameters.add("subjectfacetcount=" + subjectFacetCount);
    }
    if (filterPackageFreeAccess != null) {
      parameters.add("packagefreeaccess=" + filterPackageFreeAccess);
    }
    if (filterPrimaryPackage) {
      parameters.add("isprimarypackage=true");
    }
    parameters.add(query);

    return String.join("&", parameters);
  }
}
