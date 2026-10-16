package org.folio.holdingsiq.service.impl.urlbuilder;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.requireNonNullElse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.folio.holdingsiq.model.FilterFreeAccess;
import org.folio.holdingsiq.model.FilterSelected;
import org.folio.holdingsiq.model.PackageFilter;
import org.folio.holdingsiq.model.PackageFilterType;
import org.folio.holdingsiq.model.PackageFilterVisibility;
import org.folio.holdingsiq.model.PackageSearchField;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.SearchType;

public class PackagesFilterableUrlBuilder extends FilterableUrlBuilder {

  private static final String SUBJECT_FACET = "subject";
  private static final String SUBJECT_FILTER_PARAM = "subjectfilter";
  private static final String SUBJECT_SCHEMA_PARAM = "subjectschema";
  private static final int SUBJECT_FACET_COUNT_DEFAULT = 1000;

  private final FilterFreeAccess filterPackageFreeAccess;
  private final boolean filterPrimaryPackage;
  private final PackageFilterVisibility filterVisibility;
  private final FilterSelected filterSelected;
  private final PackageFilterType filterType;
  private final SearchType searchType;
  private final PackageSearchField searchField;
  private final String highlightTag;
  private final boolean includeSubjectFacet;
  private final int subjectFacetCount;
  private final List<Integer> filterSubjectIds;
  private final List<String> filterSubjectSchemas;

  public PackagesFilterableUrlBuilder(PackageFilter packageFilter, Pageable pageable) {
    super(packageFilter.query(), pageable, "packagename");
    this.filterPackageFreeAccess = packageFilter.filterPackageFreeAccess();
    this.filterPrimaryPackage = TRUE.equals(packageFilter.filterPrimaryPackage());
    this.filterVisibility = packageFilter.filterVisibility();
    this.filterSelected = requireNonNullElse(packageFilter.filterSelected(), FilterSelected.ALL);
    this.filterType = requireNonNullElse(packageFilter.filterType(), PackageFilterType.ALL);
    this.searchType = requireNonNullElse(packageFilter.searchType(), SearchType.ADVANCED);
    this.searchField = requireNonNullElse(packageFilter.searchField(), PackageSearchField.NAME);
    this.highlightTag = packageFilter.highlightTag();
    this.includeSubjectFacet = TRUE.equals(packageFilter.includeSubjectFacet());
    this.subjectFacetCount = packageFilter.subjectFacetCount() != null
                             ? packageFilter.subjectFacetCount()
                             : SUBJECT_FACET_COUNT_DEFAULT;
    this.filterSubjectIds = packageFilter.filterSubjectIds();
    this.filterSubjectSchemas = packageFilter.filterSubjectSchemas();
  }

  @Override
  protected void addFilterParameters(List<String> parameters) {
    addParameter(parameters, "selection", filterSelected.getValue());
    addParameter(parameters, "contenttype", filterType.getValue());
    addParameter(parameters, "searchtype", searchType.getValue());
    addParameter(parameters, "searchfield", searchField.getValue());
    addParameterIfPresent(parameters, "visibilitytype", filterVisibility, PackageFilterVisibility::getValue);
    addParameterIfPresent(parameters, "highlighttag", highlightTag, String::valueOf);
    if (includeSubjectFacet) {
      addParameter(parameters, "facetstoinclude", SUBJECT_FACET);
      addParameter(parameters, "subjectfacetcount", subjectFacetCount);
    }
    addListParameterIfNotEmpty(parameters, SUBJECT_FILTER_PARAM, filterSubjectIds, String::valueOf);
    addListParameterIfNotEmpty(parameters, SUBJECT_SCHEMA_PARAM, filterSubjectSchemas,
      schema -> URLEncoder.encode(schema, StandardCharsets.UTF_8));
    addParameterIfPresent(parameters, "packagefreeaccess", filterPackageFreeAccess, FilterFreeAccess::getValue);
    if (filterPrimaryPackage) {
      addParameter(parameters, "isprimarypackage", true);
    }
  }
}
