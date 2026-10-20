package org.folio.holdingsiq.service.impl.urlbuilder;

import static java.util.Objects.requireNonNullElse;

import java.util.List;
import org.folio.holdingsiq.model.FilterFreeAccess;
import org.folio.holdingsiq.model.FilterSelected;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.ResourceFilter;
import org.folio.holdingsiq.model.TitleFilterType;
import org.folio.holdingsiq.model.TitleSearchField;

public class ResourcesFilterableUrlBuilder extends FilterableUrlBuilder {

  private static final String RESOURCE_TYPE_PARAM = "resourcetype";

  private final FilterSelected filterSelected;
  private final TitleFilterType filterType;
  private final FilterFreeAccess filterPackageFreeAccess;
  private final TitleSearchField searchField;

  public ResourcesFilterableUrlBuilder(ResourceFilter resourceFilter, Pageable pageable) {
    super(resourceFilter.query(), pageable, TitleSearchField.TITLE_NAME.getValue());
    this.filterSelected = requireNonNullElse(resourceFilter.filterSelected(), FilterSelected.ALL);
    this.filterType = requireNonNullElse(resourceFilter.filterType(), TitleFilterType.ALL);
    this.filterPackageFreeAccess = resourceFilter.filterPackageFreeAccess();
    this.searchField = requireNonNullElse(resourceFilter.searchField(), TitleSearchField.TITLE_NAME);
  }

  @Override
  protected void addFilterParameters(List<String> parameters) {
    addParameter(parameters, SEARCH_FIELD_PARAM, searchField.getValue());
    addParameter(parameters, SELECTION_PARAM, filterSelected.getValue());
    addParameter(parameters, RESOURCE_TYPE_PARAM, filterType.getValue());
    addParameterIfPresent(parameters, PACKAGE_FREE_ACCESS_PARAM, filterPackageFreeAccess, FilterFreeAccess::getValue);
  }
}
