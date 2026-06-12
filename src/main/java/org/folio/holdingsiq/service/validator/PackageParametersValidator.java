package org.folio.holdingsiq.service.validator;

import java.util.Objects;
import org.folio.holdingsiq.model.PackageFilterSelected;
import org.folio.holdingsiq.model.PackageFilterType;
import org.folio.holdingsiq.model.PackageSearchField;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.service.exception.RequestValidationException;

public class PackageParametersValidator {

  public void validate(String filterSelected, String filterType,
                       String sort, String query, String searchField) {

    if (!Sort.contains(sort.toUpperCase())) {
      throw new RequestValidationException("Invalid Query Parameter for sort");
    }
    if ("".equals(query)) {
      throw new RequestValidationException("Search parameter cannot be empty");
    }
    if (Objects.nonNull(filterType) && !PackageFilterType.contains(filterType)) {
      throw new RequestValidationException("Invalid Query Parameter for filter[type]");
    }
    if (Objects.nonNull(filterSelected) && !PackageFilterSelected.contains(filterSelected)) {
      throw new RequestValidationException("Invalid Query Parameter for filter[selected]");
    }
    if (Objects.nonNull(searchField) && !PackageSearchField.contains(searchField)) {
      throw new RequestValidationException("Invalid Query Parameter for searchfield");
    }
  }
}

