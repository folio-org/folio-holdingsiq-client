package org.folio.holdingsiq.service;

import java.util.concurrent.CompletableFuture;

import org.folio.holdingsiq.model.PackageId;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.Title;
import org.folio.holdingsiq.model.TitleFilter;
import org.folio.holdingsiq.model.TitlePost;
import org.folio.holdingsiq.model.Titles;

public interface TitlesHoldingsIQService {

  CompletableFuture<Title> retrieveTitle(int titleId);

  CompletableFuture<Title> retrieveTitleWithRelationships(int titleId);

  CompletableFuture<Titles> retrieveTitles(TitleFilter titleFilter, Pageable pageable);

  CompletableFuture<Title> postTitle(TitlePost titlePost, PackageId packageId);
}
