package org.folio.holdingsiq.service.impl.urlbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.folio.holdingsiq.model.Pageable;

/**
 * Base for query string builders of filterable searches: filter parameters first, then the search and paging
 * parameters built by {@link QueryableUrlBuilder}.
 */
public abstract class FilterableUrlBuilder {

  protected static final String SELECTION_PARAM = "selection";
  protected static final String SEARCH_TYPE_PARAM = "searchtype";
  protected static final String SEARCH_FIELD_PARAM = "searchfield";
  protected static final String PACKAGE_FREE_ACCESS_PARAM = "packagefreeaccess";

  private final String query;
  private final Pageable pageable;
  private final String sortByNameParameter;

  protected FilterableUrlBuilder(String query, Pageable pageable, String sortByNameParameter) {
    this.query = query;
    this.pageable = pageable;
    this.sortByNameParameter = sortByNameParameter;
  }

  public final String build() {
    List<String> parameters = new ArrayList<>();
    addFilterParameters(parameters);
    parameters.add(new QueryableUrlBuilder()
      .q(query)
      .page(pageable.page())
      .count(pageable.count())
      .sort(pageable.sort())
      .nameParameter(sortByNameParameter)
      .build());
    return String.join("&", parameters);
  }

  protected abstract void addFilterParameters(List<String> parameters);

  protected static void addParameter(List<String> parameters, String name, Object value) {
    parameters.add(name + "=" + value);
  }

  protected static <T> void addParameterIfPresent(List<String> parameters, String name, T value,
                                                  Function<T, String> toValue) {
    if (value != null) {
      addParameter(parameters, name, toValue.apply(value));
    }
  }

  /**
   * Adds a comma-separated list parameter, skipping {@code null} elements; omits the parameter when nothing remains.
   */
  protected static <T> void addListParameterIfNotEmpty(List<String> parameters, String name, List<T> values,
                                                       Function<T, String> toValue) {
    if (CollectionUtils.isEmpty(values)) {
      return;
    }
    var joined = values.stream()
      .filter(Objects::nonNull)
      .map(toValue)
      .collect(Collectors.joining(","));
    if (!joined.isEmpty()) {
      addParameter(parameters, name, joined);
    }
  }
}
