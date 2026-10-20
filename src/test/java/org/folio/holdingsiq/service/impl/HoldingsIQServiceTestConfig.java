package org.folio.holdingsiq.service.impl;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.folio.holdingsiq.model.Configuration;
import org.folio.holdingsiq.model.PackageId;
import org.folio.holdingsiq.model.PackagePost;
import org.folio.holdingsiq.model.Pageable;
import org.folio.holdingsiq.model.ResourceFilter;
import org.folio.holdingsiq.model.ResourceId;
import org.folio.holdingsiq.model.ResourcePut;
import org.folio.holdingsiq.model.Sort;
import org.folio.holdingsiq.model.TitleCreated;
import org.folio.holdingsiq.model.TitleFilter;
import org.folio.holdingsiq.model.TitlePost;
import org.folio.holdingsiq.model.VendorPut;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.RegisterExtension;

public class HoldingsIQServiceTestConfig {

  protected static final String STUB_CUSTOMER_ID = "customer_id";
  protected static final String STUB_API_KEY = "test_key";
  protected static final String STUB_BASE_URL = "https://sandbox.ebsco.io";
  protected static final int PAGE_FOR_PARAM = 1;
  protected static final int COUNT_FOR_PARAM = 5;
  protected static final int PACKAGE_ID = 2222;
  protected static final int TITLE_ID = 3333;
  protected static final int VENDOR_ID = 5555;

  @RegisterExtension
  public static WireMockExtension wm = WireMockExtension.newInstance().options(WireMockConfiguration.wireMockConfig()
    .dynamicPort()
    .notifier(new Slf4jNotifier(true))
    .withRootDirectory("src/test/resources/wiremock")).build();

  protected ResourceFilter resourceFilter = ResourceFilter.builder().build();
  protected TitleFilter titleFilterWithPackageIds = TitleFilter.builder().filterPackageIds(List.of(123, 23)).build();
  protected Pageable pageable = new Pageable(PAGE_FOR_PARAM, COUNT_FOR_PARAM, Sort.NAME);
  protected VendorPut vendorPut = VendorPut.builder().build();
  protected ResourcePut resourcePut = ResourcePut.builder().build();
  protected PackagePost packagePost = PackagePost.builder().build();
  protected TitlePost titlePost = TitlePost.builder().build();
  protected TitleCreated titleCreated = new TitleCreated(TITLE_ID);
  protected PackageId packageId = new PackageId(VENDOR_ID, PACKAGE_ID);
  protected ResourceId resourceId = new ResourceId(VENDOR_ID, PACKAGE_ID, TITLE_ID);

  @AfterEach
  void tearDown() {
    wm.resetAll();
  }

  public Configuration getConfiguration() {
    return Configuration.builder().customerId(STUB_CUSTOMER_ID).apiKey(STUB_API_KEY).url(wm.baseUrl())
      .build();
  }

  protected boolean isCompletedNormally(CompletableFuture<?> completableFuture) {
    await().atMost(5, TimeUnit.SECONDS).until(completableFuture::isDone);
    return completableFuture.isDone() && !completableFuture.isCompletedExceptionally()
           && !completableFuture.isCancelled();
  }

  protected String getJson(String fileName) throws IOException {
    try (var resourceAsStream = getClass().getResourceAsStream("/wiremock/__files/" + fileName)) {
      assertNotNull(resourceAsStream);
      return new String(resourceAsStream.readAllBytes());
    } catch (Exception e) {
      throw new IOException("Failed to read file " + fileName, e);
    }
  }
}
