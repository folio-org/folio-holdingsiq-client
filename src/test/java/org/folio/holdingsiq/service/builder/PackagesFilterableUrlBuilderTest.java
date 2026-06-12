package org.folio.holdingsiq.service.builder;

import static org.folio.holdingsiq.model.PackageFilterSelected.ORDERED_THROUGH_EBSCO;
import static org.folio.holdingsiq.model.PackageFilterSelected.SELECTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.folio.holdingsiq.model.PackageFilter;
import org.folio.holdingsiq.model.PackageFilterType;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.service.impl.urlbuilder.PackagesFilterableUrlBuilder;
import org.junit.jupiter.api.Test;

class PackagesFilterableUrlBuilderTest {

  @Test
  void shouldBuildUrlWithFilterSelectedTrue() {
    String url = new PackagesFilterableUrlBuilder(PackageFilter.builder()
      .filterSelected(SELECTED).build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals("selection=selected&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=1&count=25&orderby=packagename",
      url);
  }

  @Test
  void shouldBuildUrlWithFilterSelectedAndTypeDefault() {
    String url = new PackagesFilterableUrlBuilder(PackageFilter.builder().build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals("selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=1&count=25&orderby=packagename",
      url);
  }

  @Test
  void shouldBuildUrlWithFilterSelectedEBSCO() {
    String url = new PackagesFilterableUrlBuilder(PackageFilter.builder()
      .filterSelected(ORDERED_THROUGH_EBSCO).build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals(
      "selection=orderedthroughebsco&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=1&count=25&orderby=packagename",
      url);
  }

  @Test
  void shouldBuildUrlWithFilterType() {
    String url = new PackagesFilterableUrlBuilder(PackageFilter.builder()
      .filterType(PackageFilterType.ABSTRACT_AND_INDEX).build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals(
      "selection=all&contenttype=abstractandindex&searchtype=advanced&searchfield=name&search=&offset=1&count=25&orderby=packagename",
      url);
  }

  @Test
  void shouldBuildUrlWithCount() {
    String url = new PackagesFilterableUrlBuilder(PackageFilter.builder().build(),
      new Pageable(1, 5, Sort.NAME))
      .build();
    assertEquals("selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=1&count=5&orderby=packagename", url);
  }

  @Test
  void shouldBuildUrlWithSort() {
    String url = new PackagesFilterableUrlBuilder(PackageFilter.builder()
      .query("Academic").build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals(
      "selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=Academic&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithPage() {
    String url = new PackagesFilterableUrlBuilder(PackageFilter.builder().build(),
      new Pageable(2, 25, Sort.NAME))
      .build();
    assertEquals("selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=2&count=25&orderby=packagename",
      url);
  }

  @Test
  void shouldBuildUrlWithQABCCLIO() {
    String url = new PackagesFilterableUrlBuilder(PackageFilter.builder()
      .query("ABC-CLIO").build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals(
      "selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=ABC-CLIO&offset=1&count=25&orderby=relevance", url);
  }
}
