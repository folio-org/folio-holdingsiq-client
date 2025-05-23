package org.folio.holdingsiq.service.impl.urlbuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.folio.holdingsiq.model.Sort;

public class QueryableUrlBuilder {

  private static final String RELEVANCE_PARAMETER = "relevance";
  private String q;
  private int page = 1;
  private int count = 25;
  private Sort sort;
  private String nameParameter;

  public QueryableUrlBuilder q(String q) {
    this.q = q;
    return this;
  }

  public QueryableUrlBuilder page(int page) {
    this.page = page;
    return this;
  }

  public QueryableUrlBuilder count(int count) {
    this.count = count;
    return this;
  }

  public QueryableUrlBuilder sort(Sort sort) {
    this.sort = sort;
    return this;
  }

  public QueryableUrlBuilder nameParameter(String nameParameter) {
    this.nameParameter = nameParameter;
    return this;
  }

  public String build() {
    List<String> parameters = new ArrayList<>();
    if (StringUtils.isNoneBlank(q)) {
      String encodedQuery = URLEncoder.encode(q, StandardCharsets.UTF_8);
      parameters.add("search=" + encodedQuery);
    } else {
      parameters.add("search=");
    }

    parameters.add("offset=" + page);
    parameters.add("count=" + count);
    parameters.add("orderby=" + determineSortValue(sort, q));

    return String.join("&", parameters);
  }

  private String determineSortValue(Sort sort, String query) {
    if (query == null) {
      return nameParameter;
    }
    return switch (sort) {
      case RELEVANCE -> RELEVANCE_PARAMETER;
      case NAME -> nameParameter;
    };
  }
}
