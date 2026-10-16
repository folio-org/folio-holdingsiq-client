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
    addParameter(parameters, "searchfield", searchField.getValue());
    addParameter(parameters, "selection", filterSelected.getValue());
    addParameter(parameters, "resourcetype", filterType.getValue());
    addParameterIfPresent(parameters, "packagefreeaccess", filterPackageFreeAccess, FilterFreeAccess::getValue);
  }
}
