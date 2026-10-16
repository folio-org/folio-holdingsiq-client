package org.folio.holdingsiq.service.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.folio.holdingsiq.model.FilterFreeAccess;
import org.folio.holdingsiq.model.FilterSelected;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.SearchType;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.model.TitleFilter;
import org.folio.holdingsiq.model.TitleFilterType;
import org.folio.holdingsiq.model.TitleSearchField;
import org.folio.holdingsiq.service.impl.urlbuilder.TitlesFilterableUrlBuilder;
import org.junit.jupiter.api.Test;

class TitlesFilterableUrlBuilderTest {

  @Test
  void shouldBuildUrlWithCount() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder().query("ebsco").build(),
      new Pageable(1, 5, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&search=ebsco" +
      "&offset=1&count=5&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithSort() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder().query("ebsco").build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&search=ebsco" +
      "&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  void shouldBuildUrlWithPage() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder().query("ebsco").build(),
      new Pageable(2, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&search=ebsco" +
      "&offset=2&count=25&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithFilterSelectedAndType() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .query("news")
      .filterType(TitleFilterType.BOOK)
      .filterSelected(FilterSelected.SELECTED)
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=selected&resourcetype=book&searchtype=advanced&search=news" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithFilterSelectedOrderedThroughEbsco() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .filterSelected(FilterSelected.ORDERED_THROUGH_EBSCO)
      .build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals("searchfield=titlename&selection=orderedthroughebsco&resourcetype=all&searchtype=advanced&search=" +
      "&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  void shouldBuildUrlWithMultiWordFilterType() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .filterType(TitleFilterType.THESIS_DISSERTATION)
      .build(),
      new Pageable(1, 25, Sort.NAME))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=thesisdissertation&searchtype=advanced&search=" +
      "&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  void shouldBuildUrlWithSearchFieldIsxn() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .query("1362-3613")
      .searchField(TitleSearchField.ISXN)
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=isxn&selection=all&resourcetype=all&searchtype=advanced&search=1362-3613" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithSearchFieldSubject() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .query("history")
      .searchField(TitleSearchField.SUBJECT)
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=subject&selection=all&resourcetype=all&searchtype=advanced&search=history" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithSearchFieldPublisher() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .query("publisherName")
      .searchField(TitleSearchField.PUBLISHER)
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=publisher&selection=all&resourcetype=all&searchtype=advanced&search=publisherName" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithSearchFieldZdbid() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .query("2071469-8")
      .searchField(TitleSearchField.ZDBID)
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=zdbid&selection=all&resourcetype=all&searchtype=advanced&search=2071469-8" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithSearchType() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .query("ebsco")
      .searchType(SearchType.EXACT_MATCH)
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=exactmatch&search=ebsco" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  void shouldBuildUrlWithDefaultsAndSortByNameWhenNoQuery() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder().build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&search=" +
      "&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  void shouldBuildUrlWithPackageIdsFilter() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .filterPackageIds(List.of(123, 456))
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&packageidfilter=123,456" +
      "&search=&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  void shouldBuildUrlWithPackageFreeAccess() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .filterPackageFreeAccess(FilterFreeAccess.TRUE)
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&packagefreeaccess=true" +
      "&search=&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  void shouldBuildUrlWithConsortia() {
    String url = new TitlesFilterableUrlBuilder(TitleFilter.builder()
      .filterConsortia(List.of("KERIS", "Big Ten"))
      .build(),
      new Pageable(1, 25, Sort.RELEVANCE))
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&consortia=KERIS,Big+Ten" +
      "&search=&offset=1&count=25&orderby=titlename", url);
  }
}
