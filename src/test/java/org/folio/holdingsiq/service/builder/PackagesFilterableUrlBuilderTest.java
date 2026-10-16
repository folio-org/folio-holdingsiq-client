package org.folio.holdingsiq.service.builder;

import static java.util.Collections.singletonList;
import static org.folio.holdingsiq.model.FilterSelected.ORDERED_THROUGH_EBSCO;
import static org.folio.holdingsiq.model.FilterSelected.SELECTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;

import java.util.List;
import java.util.stream.Stream;
import org.folio.holdingsiq.model.FilterFreeAccess;
import org.folio.holdingsiq.model.PackageFilter;
import org.folio.holdingsiq.model.PackageFilterType;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.service.impl.urlbuilder.PackagesFilterableUrlBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PackagesFilterableUrlBuilderTest {

  private static final Pageable DEFAULT_PAGEABLE = new Pageable(1, 25, Sort.NAME);
  private static final String DEFAULT_FILTER_PARAMS = "selection=all&contenttype=all&searchtype=advanced&searchfield=name";
  private static final String DEFAULT_PAGING_PARAMS = "&search=&offset=1&count=25&orderby=packagename";
  private static final String SUBJECT_FACET_PARAMS = "&facetstoinclude=subject&subjectfacetcount=1000";

  @ParameterizedTest
  @MethodSource("filtersAndPageables")
  void shouldBuildUrl(PackageFilter filter, Pageable pageable, String expectedUrl) {
    String url = new PackagesFilterableUrlBuilder(filter, pageable).build();
    assertEquals(expectedUrl, url);
  }

  @ParameterizedTest
  @MethodSource("subjectFilters")
  void shouldBuildUrlWithSubjectParameters(PackageFilter filter, String subjectParameters) {
    String url = new PackagesFilterableUrlBuilder(filter, DEFAULT_PAGEABLE).build();
    assertEquals(DEFAULT_FILTER_PARAMS + subjectParameters + DEFAULT_PAGING_PARAMS, url);
  }

  @ParameterizedTest
  @MethodSource("emptySubjectFilters")
  void shouldOmitSubjectParametersWhenNoValues(PackageFilter filter) {
    String url = new PackagesFilterableUrlBuilder(filter, DEFAULT_PAGEABLE).build();
    assertEquals(DEFAULT_FILTER_PARAMS + DEFAULT_PAGING_PARAMS, url);
  }

  private static Stream<Arguments> filtersAndPageables() {
    return Stream.of(
      argumentSet("defaults",
        PackageFilter.builder().build(), DEFAULT_PAGEABLE,
        "selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=1&count=25&orderby=packagename"),
      argumentSet("selected filter",
        PackageFilter.builder().filterSelected(SELECTED).build(), DEFAULT_PAGEABLE,
        "selection=selected&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=1&count=25&orderby=packagename"),
      argumentSet("ordered through EBSCO filter",
        PackageFilter.builder().filterSelected(ORDERED_THROUGH_EBSCO).build(), DEFAULT_PAGEABLE,
        "selection=orderedthroughebsco&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=1&count=25&orderby=packagename"),
      argumentSet("content type filter",
        PackageFilter.builder().filterType(PackageFilterType.ABSTRACT_AND_INDEX).build(), DEFAULT_PAGEABLE,
        "selection=all&contenttype=abstractandindex&searchtype=advanced&searchfield=name&search=&offset=1&count=25&orderby=packagename"),
      argumentSet("custom count",
        PackageFilter.builder().build(), new Pageable(1, 5, Sort.NAME),
        "selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=1&count=5&orderby=packagename"),
      argumentSet("custom page",
        PackageFilter.builder().build(), new Pageable(2, 25, Sort.NAME),
        "selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=&offset=2&count=25&orderby=packagename"),
      argumentSet("query with relevance sort",
        PackageFilter.builder().query("Academic").build(), new Pageable(1, 25, Sort.RELEVANCE),
        "selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=Academic&offset=1&count=25&orderby=relevance"),
      argumentSet("query with hyphen",
        PackageFilter.builder().query("ABC-CLIO").build(), new Pageable(1, 25, Sort.RELEVANCE),
        "selection=all&contenttype=all&searchtype=advanced&searchfield=name&search=ABC-CLIO&offset=1&count=25&orderby=relevance"),
      argumentSet("package free access filter",
        PackageFilter.builder().filterPackageFreeAccess(FilterFreeAccess.TRUE).build(), DEFAULT_PAGEABLE,
        DEFAULT_FILTER_PARAMS + "&packagefreeaccess=true" + DEFAULT_PAGING_PARAMS)
    );
  }

  private static Stream<Arguments> subjectFilters() {
    return Stream.of(
      argumentSet("subject ids",
        PackageFilter.builder().filterSubjectIds(List.of(5207, 35408)).build(),
        "&subjectfilter=5207,35408"),
      argumentSet("subject ids with subject facet",
        PackageFilter.builder().includeSubjectFacet(true).filterSubjectIds(List.of(5207)).build(),
        SUBJECT_FACET_PARAMS + "&subjectfilter=5207"),
      argumentSet("encoded subject schemas with subject facet",
        PackageFilter.builder().includeSubjectFacet(true).filterSubjectSchemas(List.of("Library of Congress", "BISAC")).build(),
        SUBJECT_FACET_PARAMS + "&subjectschema=Library+of+Congress,BISAC"),
      argumentSet("subject ids and subject schemas",
        PackageFilter.builder().filterSubjectIds(List.of(5207)).filterSubjectSchemas(List.of("BISAC")).build(),
        "&subjectfilter=5207&subjectschema=BISAC")
    );
  }

  private static Stream<Arguments> emptySubjectFilters() {
    return Stream.of(
      argumentSet("empty subject ids", PackageFilter.builder().filterSubjectIds(List.of()).build()),
      argumentSet("only null subject ids", PackageFilter.builder().filterSubjectIds(singletonList(null)).build()),
      argumentSet("empty subject schemas", PackageFilter.builder().filterSubjectSchemas(List.of()).build()),
      argumentSet("only null subject schemas", PackageFilter.builder().filterSubjectSchemas(singletonList(null)).build())
    );
  }
}
