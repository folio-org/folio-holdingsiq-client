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

  private static final String SORT_BY_NAME_PARAM = "packagename";
  private static final String CONTENT_TYPE_PARAM = "contenttype";
  private static final String VISIBILITY_TYPE_PARAM = "visibilitytype";
  private static final String HIGHLIGHT_TAG_PARAM = "highlighttag";
  private static final String FACETS_TO_INCLUDE_PARAM = "facetstoinclude";
  private static final String SUBJECT_FACET_COUNT_PARAM = "subjectfacetcount";
  private static final String IS_PRIMARY_PACKAGE_PARAM = "isprimarypackage";

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
    super(packageFilter.query(), pageable, SORT_BY_NAME_PARAM);
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
    addParameter(parameters, SELECTION_PARAM, filterSelected.getValue());
    addParameter(parameters, CONTENT_TYPE_PARAM, filterType.getValue());
    addParameter(parameters, SEARCH_TYPE_PARAM, searchType.getValue());
    addParameter(parameters, SEARCH_FIELD_PARAM, searchField.getValue());
    addParameterIfPresent(parameters, VISIBILITY_TYPE_PARAM, filterVisibility, PackageFilterVisibility::getValue);
    addParameterIfPresent(parameters, HIGHLIGHT_TAG_PARAM, highlightTag, String::valueOf);
    if (includeSubjectFacet) {
      addParameter(parameters, FACETS_TO_INCLUDE_PARAM, SUBJECT_FACET);
      addParameter(parameters, SUBJECT_FACET_COUNT_PARAM, subjectFacetCount);
    }
    addListParameterIfNotEmpty(parameters, SUBJECT_FILTER_PARAM, filterSubjectIds, String::valueOf);
    addListParameterIfNotEmpty(parameters, SUBJECT_SCHEMA_PARAM, filterSubjectSchemas,
      schema -> URLEncoder.encode(schema, StandardCharsets.UTF_8));
    addParameterIfPresent(parameters, PACKAGE_FREE_ACCESS_PARAM, filterPackageFreeAccess, FilterFreeAccess::getValue);
    if (filterPrimaryPackage) {
      addParameter(parameters, IS_PRIMARY_PACKAGE_PARAM, true);
    }
  }
}
