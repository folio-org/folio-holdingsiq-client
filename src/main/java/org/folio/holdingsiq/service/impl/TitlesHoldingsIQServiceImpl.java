package org.folio.holdingsiq.service.impl;

import static java.util.concurrent.CompletableFuture.completedFuture;

import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.PACKAGES_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.TITLES_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.VENDORS_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.successBodyLogger;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import io.vertx.core.Vertx;

import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.FilterQuery;
import org.folio.holdingsiq.model.PackageId;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.model.Title;
import org.folio.holdingsiq.model.TitleCreated;
import org.folio.holdingsiq.model.TitlePost;
import org.folio.holdingsiq.model.Titles;
import org.folio.holdingsiq.service.TitlesHoldingsIQService;
import org.folio.holdingsiq.service.impl.urlbuilder.TitlesFilterableUrlBuilder;

public class TitlesHoldingsIQServiceImpl implements TitlesHoldingsIQService {

  private final HoldingsRequestHelper holdingsRequestHelper;

  public TitlesHoldingsIQServiceImpl(Configuration config, Vertx vertx) {
    holdingsRequestHelper = new HoldingsRequestHelper(config, vertx);
    holdingsRequestHelper.addBodyListener(successBodyLogger());
  }

  @Override
  public CompletableFuture<Title> retrieveTitle(long id) {
    final String path = TITLES_PATH + '/' + id;
    return holdingsRequestHelper.getRequest(holdingsRequestHelper.constructURL(path), Title.class);
  }

  @Override
  public CompletableFuture<Titles> retrieveTitles(String rmapiQuery) {
    var path = String.format("titles?%s", rmapiQuery);
    return getTitles(path)
      .thenCompose(titles -> completedFuture(postProcessTitles(titles)));
  }

  @Override
  public CompletableFuture<Titles> retrieveTitles(FilterQuery filterQuery, String searchType, Sort sort, int page,
                                                  int count) {
    String query = new TitlesFilterableUrlBuilder()
      .filter(filterQuery)
      .searchType(searchType)
      .sort(sort)
      .page(page)
      .count(count)
      .build();

    return getTitles(TITLES_PATH + "?" + query)
      .thenCompose(titles -> completedFuture(postProcessTitles(titles)));
  }

  @Override
  public CompletableFuture<Titles> retrieveTitles(Long providerId, Long packageId, FilterQuery filterQuery,
                                                  String searchType, Sort sort,
                                                  int page, int count) {
    String query = new TitlesFilterableUrlBuilder()
      .filter(filterQuery)
      .searchType(searchType)
      .sort(sort)
      .page(page)
      .count(count)
      .build();

    String titlesPath = VENDORS_PATH + '/' + providerId + '/' + PACKAGES_PATH + '/' + packageId + '/' + TITLES_PATH;

    return getTitles(titlesPath + "?" + query)
      .thenCompose(titles -> completedFuture(postProcessTitles(titles)));
  }

  @Override
  public CompletableFuture<Title> postTitle(TitlePost titlePost, PackageId packageId) {
    return this.createTitle(titlePost, packageId).thenCompose(titleCreated -> retrieveTitle(titleCreated.getTitleId()));
  }

  private CompletableFuture<Titles> getTitles(String path) {
    return holdingsRequestHelper.getRequest(holdingsRequestHelper.constructURL(path), Titles.class);
  }

  private Titles postProcessTitles(Titles titles) {
    int initialSize = titles.getTitleList().size();
    removeInvalidObjects(titles);
    final Integer totalResults = getTotalResults(titles, initialSize);
    return titles.toBuilder().totalResults(totalResults).build();
  }

  private void removeInvalidObjects(Titles titles) {
    Predicate<Title> isNullPredicate = Objects::isNull;
    Predicate<Title> isEmptyCustomerResourcesListPredicate = title -> title.getCustomerResourcesList().isEmpty();

    titles.getTitleList().removeIf((isNullPredicate).or(isEmptyCustomerResourcesListPredicate));
  }

  private Integer getTotalResults(Titles titles, int initialSize) {

    final int sizeAfterRemove = titles.getTitleList().size();
    final int removedCount = initialSize - sizeAfterRemove;

    return Optional.ofNullable(titles.getTotalResults())
      .map(total -> total - removedCount)
      .orElse(0);
  }

  private CompletableFuture<TitleCreated> createTitle(TitlePost entity, PackageId packageId) {
    final String path = VENDORS_PATH + '/' + packageId.getProviderIdPart() + '/' + PACKAGES_PATH + '/' + packageId
      .getPackageIdPart() + '/' + TITLES_PATH;
    return holdingsRequestHelper.postRequest(holdingsRequestHelper.constructURL(path), entity, TitleCreated.class);
  }
}
