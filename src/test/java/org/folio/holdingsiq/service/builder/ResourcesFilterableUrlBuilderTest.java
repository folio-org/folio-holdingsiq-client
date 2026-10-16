package org.folio.holdingsiq.service.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.folio.holdingsiq.model.FilterFreeAccess;
import org.folio.holdingsiq.model.FilterSelected;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.ResourceFilter;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.model.TitleFilterType;
import org.folio.holdingsiq.model.TitleSearchField;
import org.folio.holdingsiq.service.impl.urlbuilder.ResourcesFilterableUrlBuilder;
import org.junit.jupiter.api.Test;

class ResourcesFilterableUrlBuilderTest {

  @Test
  void shouldBuildUrlWithDefaultsAndSortByNameWhenNoQuery() {
    String url = new ResourcesFilterableUrlBuilder(ResourceFilter.builder().build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&search=&offset=1&count=25&orderby=titlename",
      url);
  }

  @Test
  void shouldBuildUrlWithQueryAndSort() {
    String url = new ResourcesFilterableUrlBuilder(ResourceFilter.builder().query("ebsco").build(),
      new Pageable(2, 5, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&search=ebsco&offset=2&count=5&orderby=relevance",
      url);
  }

  @Test
  void shouldBuildUrlWithSearchFieldSelectionAndType() {
    String url = new ResourcesFilterableUrlBuilder(ResourceFilter.builder()
      .query("1362-3613")
      .searchField(TitleSearchField.ISXN)
      .filterSelected(FilterSelected.NOT_SELECTED)
      .filterType(TitleFilterType.JOURNAL)
      .build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals("searchfield=isxn&selection=notselected&resourcetype=journal&search=1362-3613" +
      "&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  void shouldBuildUrlWithPackageFreeAccess() {
    String url = new ResourcesFilterableUrlBuilder(ResourceFilter.builder()
      .filterPackageFreeAccess(FilterFreeAccess.FALSE)
      .build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&packagefreeaccess=false" +
      "&search=&offset=1&count=25&orderby=titlename", url);
  }
}
