package org.folio.holdingsiq.service.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.service.impl.urlbuilder.QueryableUrlBuilder;
import org.junit.jupiter.api.Test;

class QueryableUrlBuilderTest {
  @Test
  void shouldBuildUrlForNameSortWhenSortName() {
    String path = new QueryableUrlBuilder()
      .q("ebsco")
      .nameParameter("vendorname")
      .sort(Sort.NAME)
      .build();
    assertEquals("search=ebsco&offset=1&count=25&orderby=vendorname", path);
  }

  @Test
  void shouldBuildUrlForRelevanceSortWhenSortRelevance() {
    String path = new QueryableUrlBuilder()
      .sort(Sort.RELEVANCE)
      .q("ebsco")
      .build();
    assertEquals("search=ebsco&offset=1&count=25&orderby=relevance", path);
  }

  @Test
  void shouldBuildUrlForNameSortWhenQueryIsNotSet() {
    String path = new QueryableUrlBuilder()
      .nameParameter("vendorname")
      .build();
    assertEquals("search=&offset=1&count=25&orderby=vendorname", path);
  }
}
