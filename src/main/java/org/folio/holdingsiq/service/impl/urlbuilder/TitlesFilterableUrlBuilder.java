package org.folio.holdingsiq.service.impl.urlbuilder;

import static java.util.Objects.requireNonNullElse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.folio.holdingsiq.model.FilterFreeAccess;
import org.folio.holdingsiq.model.FilterSelected;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.SearchType;
import org.folio.holdingsiq.model.TitleFilter;
import org.folio.holdingsiq.model.TitleFilterType;
import org.folio.holdingsiq.model.TitleSearchField;

public class TitlesFilterableUrlBuilder extends FilterableUrlBuilder {

  private static final String RESOURCE_TYPE_PARAM = "resourcetype";
  private static final String PACKAGE_ID_FILTER_PARAM = "packageidfilter";
  private static final String CONSORTIA_PARAM = "consortia";

  private final FilterSelected filterSelected;
  private final TitleFilterType filterType;
  private final FilterFreeAccess filterPackageFreeAccess;
  private final List<Integer> filterPackageIds;
  private final List<String> filterConsortia;
  private final SearchType searchType;
  private final TitleSearchField searchField;

  public TitlesFilterableUrlBuilder(TitleFilter titleFilter, Pageable pageable) {
    super(titleFilter.query(), pageable, TitleSearchField.TITLE_NAME.getValue());
    this.filterSelected = requireNonNullElse(titleFilter.filterSelected(), FilterSelected.ALL);
    this.filterType = requireNonNullElse(titleFilter.filterType(), TitleFilterType.ALL);
    this.filterPackageFreeAccess = titleFilter.filterPackageFreeAccess();
    this.filterPackageIds = titleFilter.filterPackageIds();
    this.filterConsortia = titleFilter.filterConsortia();
    this.searchType = requireNonNullElse(titleFilter.searchType(), SearchType.ADVANCED);
    this.searchField = requireNonNullElse(titleFilter.searchField(), TitleSearchField.TITLE_NAME);
  }

  @Override
  protected void addFilterParameters(List<String> parameters) {
    addParameter(parameters, SEARCH_FIELD_PARAM, searchField.getValue());
    addParameter(parameters, SELECTION_PARAM, filterSelected.getValue());
    addParameter(parameters, RESOURCE_TYPE_PARAM, filterType.getValue());
    addParameter(parameters, SEARCH_TYPE_PARAM, searchType.getValue());
    addListParameterIfNotEmpty(parameters, PACKAGE_ID_FILTER_PARAM, filterPackageIds, String::valueOf);
    addParameterIfPresent(parameters, PACKAGE_FREE_ACCESS_PARAM, filterPackageFreeAccess, FilterFreeAccess::getValue);
    addListParameterIfNotEmpty(parameters, CONSORTIA_PARAM, filterConsortia,
      consortia -> URLEncoder.encode(consortia, StandardCharsets.UTF_8));
  }
}
