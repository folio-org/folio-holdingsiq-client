package org.folio.holdingsiq.service.validator;

import java.util.Objects;
import org.folio.holdingsiq.model.FilterSelected;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.model.TitleFilterType;
import org.folio.holdingsiq.model.TitleSearchField;
import org.folio.holdingsiq.service.exception.RequestValidationException;

public class TitleParametersValidator {

  /**
   * @throws RequestValidationException if validation fails
   */
  public void validate(String filterSelected, String filterType,
                       String sort, String query, String searchField) {

    if (!Sort.contains(sort.toUpperCase())) {
      throw new RequestValidationException("Invalid Query Parameter for sort");
    }
    if ("".equals(query)) {
      throw new RequestValidationException("Search parameter cannot be empty");
    }
    if (Objects.nonNull(filterType) && !TitleFilterType.contains(filterType)) {
      throw new RequestValidationException("Invalid Query Parameter for filter[type]");
    }
    if (Objects.nonNull(filterSelected) && !FilterSelected.contains(filterSelected)) {
      throw new RequestValidationException("Invalid Query Parameter for filter[selected]");
    }
    if (Objects.nonNull(searchField) && !TitleSearchField.contains(searchField)) {
      throw new RequestValidationException("Invalid Query Parameter for searchfield");
    }
  }
}
