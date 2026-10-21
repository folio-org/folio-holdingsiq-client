package org.folio.holdingsiq.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A resource: a title within a single list (package), as returned by the v2
 * {@code /lists/{listid}/titles} and {@code /lists/{listid}/titles/{kbid}} endpoints.
 * Title metadata and the list-specific data share one level.
 */
@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor(onConstructor_ = @JsonIgnore)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource {

  @JsonProperty("kbid")
  private Integer titleId;

  @JsonProperty("titleName")
  private String titleName;

  @JsonProperty("publisherName")
  private String publisherName;

  @JsonProperty("identifiersList")
  private List<Identifier> identifiersList;

  @JsonProperty("subjectsList")
  private List<Subject> subjectsList;

  @JsonProperty("isTitleCustom")
  private Boolean isTitleCustom;

  @JsonProperty("alternateTitleList")
  private List<AlternateTitle> alternateTitleList;

  @JsonProperty("pubType")
  private String pubType;

  @JsonProperty("description")
  private String description;

  @JsonProperty("edition")
  private String edition;

  @JsonProperty("isPeerReviewed")
  private Boolean isPeerReviewed;

  @JsonProperty("contributorsList")
  private List<Contributor> contributorsList;

  @JsonProperty("listId")
  private Integer packageId;

  @JsonProperty("packageName")
  private String packageName;

  @JsonProperty("packageType")
  private String packageType;

  @JsonProperty("proxy")
  private ProxyUrl proxy;

  @JsonProperty("isPrimaryPackage")
  private Boolean isPrimaryPackage;

  @JsonProperty("isPackageCustom")
  private Boolean isPackageCustom;

  @JsonProperty("vendorId")
  private Integer vendorId;

  @JsonProperty("vendorName")
  private String vendorName;

  @JsonProperty("locationId")
  private Integer locationId;

  @JsonProperty("isSelected")
  private Boolean isSelected;

  @JsonProperty("isOrderedThroughEbsco")
  private Boolean isOrderedThroughEbsco;

  @JsonProperty("isTokenNeeded")
  private Boolean isTokenNeeded;

  @JsonProperty("kbartExcluded")
  private Boolean kbartExcluded;

  @JsonProperty("kbartCoverageOwnership")
  private Boolean kbartCoverageOwnership;

  @JsonProperty("customCoverageOwnership")
  private String customCoverageOwnership;

  @JsonProperty("visibility")
  private List<Visibility> visibilityDetails;

  @JsonProperty("managedCoverageList")
  private List<CoverageDates> managedCoverageList;

  @JsonProperty("customCoverageList")
  private List<CoverageDates> customCoverageList;

  @JsonProperty("coverageStatement")
  private String coverageStatement;

  @JsonProperty("managedEmbargoPeriod")
  private EmbargoPeriod managedEmbargoPeriod;

  @JsonProperty("customEmbargoPeriod")
  private EmbargoPeriod customEmbargoPeriod;

  @JsonProperty("url")
  private String url;

  @JsonProperty("consortia")
  private String consortia;

  @JsonUnwrapped
  private UserDefinedFields userDefinedFields;

  @JsonProperty("titleRelationships")
  private List<TitleRelationship> titleRelationships;
}
