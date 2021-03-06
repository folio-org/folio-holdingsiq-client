package org.folio.holdingsiq.service.impl.urlbuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.folio.holdingsiq.model.Sort;

public class PackagesFilterableUrlBuilder {

  private String filterSelected;
  private String filterType;
  private int page = 1;
  private int count = 25;
  private Sort sort;
  private String q;

  public PackagesFilterableUrlBuilder filterSelected(String filterSelected) {
    this.filterSelected = filterSelected;
    return this;
  }

  public PackagesFilterableUrlBuilder filterType(String filterType) {
    this.filterType = filterType;
    return this;
  }

  public PackagesFilterableUrlBuilder page(int page) {
    this.page = page;
    return this;
  }

  public PackagesFilterableUrlBuilder count(int count) {
    this.count = count;
    return this;
  }

  public PackagesFilterableUrlBuilder sort(Sort sort) {
    this.sort = sort;
    return this;
  }

  public PackagesFilterableUrlBuilder q(String q) {
    this.q = q;
    return this;
  }

  public String build(){

    String selection = StringUtils.defaultIfEmpty(filterSelected, "all");
    String contentType = StringUtils.defaultIfEmpty(filterType, "all");
    List<String> parameters = new ArrayList<>();

    parameters.add("selection=" + selection);
    parameters.add("contenttype=" + contentType);
    String query = new QueryableUrlBuilder()
      .q(q)
      .page(page)
      .count(count)
      .sort(sort)
      .nameParameter("packagename")
      .build();
    parameters.add(query);
    return  String.join("&", parameters);
  }
}
