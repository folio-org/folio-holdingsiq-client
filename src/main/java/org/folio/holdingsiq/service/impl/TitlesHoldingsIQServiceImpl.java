package org.folio.holdingsiq.service.impl;

import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.PACKAGES_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.TITLES_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.VENDORS_PATH;
import static org.folio.holdingsiq.service.impl.HoldingsRequestHelper.successBodyLogger;

import io.vertx.core.Vertx;
import java.util.concurrent.CompletableFuture;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.PackageId;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.Title;
import org.folio.holdingsiq.model.TitleCreated;
import org.folio.holdingsiq.model.TitleFilter;
import org.folio.holdingsiq.model.TitlePost;
import org.folio.holdingsiq.model.Titles;
import org.folio.holdingsiq.service.TitlesHoldingsIQService;
import org.folio.holdingsiq.service.impl.urlbuilder.TitlesFilterableUrlBuilder;

public class TitlesHoldingsIQServiceImpl implements TitlesHoldingsIQService {

  private static final String INCLUDE_RELATIONSHIPS_PARAM = "includerelationships=true";

  private final HoldingsRequestHelper holdingsRequestHelper;

  public TitlesHoldingsIQServiceImpl(Configuration config, Vertx vertx) {
    holdingsRequestHelper = new HoldingsRequestHelper(config, vertx);
    holdingsRequestHelper.addBodyListener(successBodyLogger());
  }

  @Override
  public CompletableFuture<Title> retrieveTitle(int titleId) {
    return getTitle(titleId, false);
  }

  @Override
  public CompletableFuture<Title> retrieveTitleWithRelationships(int titleId) {
    return getTitle(titleId, true);
  }

  @Override
  public CompletableFuture<Titles> retrieveTitles(TitleFilter titleFilter, Pageable pageable) {
    var queryParams = new TitlesFilterableUrlBuilder(titleFilter, pageable).build();
    var requestUrl = holdingsRequestHelper.constructURLv2(TITLES_PATH, queryParams);
    return holdingsRequestHelper.getRequest(requestUrl, Titles.class);
  }

  /**
   * Creates a custom title. There is no v2 create endpoint, so the title is created via v1 and then
   * retrieved via v2; the v1 titleId and the v2 kbid are the same identifier.
   */
  @Override
  public CompletableFuture<Title> postTitle(TitlePost titlePost, PackageId packageId) {
    var titlePath = VENDORS_PATH + '/' + packageId.providerIdPart() + '/'
                    + PACKAGES_PATH + '/' + packageId.packageIdPart() + '/'
                    + TITLES_PATH;
    var requestUrl = holdingsRequestHelper.constructURL(titlePath);
    return holdingsRequestHelper.postRequest(requestUrl, titlePost, TitleCreated.class)
      .thenCompose(titleCreated -> retrieveTitle(titleCreated.titleId()));
  }

  private CompletableFuture<Title> getTitle(int titleId, boolean includeRelationships) {
    final String path = TITLES_PATH + '/' + titleId;
    var requestUrl = includeRelationships
                     ? holdingsRequestHelper.constructURLv2(path, INCLUDE_RELATIONSHIPS_PARAM)
                     : holdingsRequestHelper.constructURLv2(path);
    return holdingsRequestHelper.getRequest(requestUrl, Title.class);
  }
}
