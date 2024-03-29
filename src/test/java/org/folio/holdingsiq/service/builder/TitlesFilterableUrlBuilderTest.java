package org.folio.holdingsiq.service.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.folio.holdingsiq.model.FilterQuery;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.service.impl.urlbuilder.TitlesFilterableUrlBuilder;

import java.util.List;

public class TitlesFilterableUrlBuilderTest {

  private FilterQuery.FilterQueryBuilder fqb;

  @Before
  public void setUp() {
    fqb = FilterQuery.builder();
  }

  @Test
  public void shouldBuildUrlWithCount() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.name("ebsco").build())
      .count(5)
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&search=ebsco" +
      "&offset=1&count=5&orderby=relevance", url);
  }

  @Test
  public void shouldBuildUrlWithSort() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.name("ebsco").build())
      .sort(Sort.NAME)
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&search=ebsco" +
      "&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  public void shouldBuildUrlWithPage() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.name("ebsco").build())
      .page(2)
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&search=ebsco" +
      "&offset=2&count=25&orderby=relevance", url);
  }

  @Test
  public void shouldBuildUrlForFilterBySelectedStatus() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.name("news").type("book").selected("selected").build())
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=titlename&selection=selected&resourcetype=book&searchtype=advanced&search=news" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  public void shouldBuildUrlForFilterByIsxn() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.isxn("1362-3613").build())
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=isxn&selection=all&resourcetype=all&searchtype=advanced&search=1362-3613" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  public void shouldBuildUrlForFilterBySubject() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.subject("history").build())
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=subject&selection=all&resourcetype=all&searchtype=advanced&search=history" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  public void shouldBuildUrlForFilterByPublisher() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.publisher("publisherName").build())
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=publisher&selection=all&resourcetype=all&searchtype=advanced&search=publisherName" +
      "&offset=1&count=25&orderby=relevance", url);
  }

  @Test
  public void shouldBuildUrlForFilterByType() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.type("book").build())
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=book&searchtype=advanced&search=" +
      "&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  public void shouldBuildUrlforDefaultTitleSearchAndSort() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.build())
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&search=" +
      "&offset=1&count=25&orderby=titlename", url);
  }

  @Test
  public void shouldBuildUrlPackageIdsFilter() {
    String url = new TitlesFilterableUrlBuilder()
      .filter(fqb.packageIds(List.of(123, 456)).build())
      .sort(Sort.RELEVANCE)
      .build();
    assertEquals("searchfield=titlename&selection=all&resourcetype=all&searchtype=advanced&packageidfilter=123,456" +
      "&search=&offset=1&count=25&orderby=titlename", url);
  }
}
