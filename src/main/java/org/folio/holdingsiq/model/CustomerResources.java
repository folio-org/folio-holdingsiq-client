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

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor(onConstructor_ =@JsonIgnore)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerResources {

  @JsonProperty("kbid")
  private Integer titleId;

  @JsonProperty("listId")
  private Integer packageId;

  @JsonProperty("packageName")
  private String packageName;

  @JsonProperty("packageType")
  private String packageType;

  @JsonProperty("proxy")
  private ProxyUrl proxy;

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

  @JsonProperty("isPrimaryPackage")
  private Boolean isPrimaryPackage;

  @JsonProperty("kbartExcluded")
  private Boolean kbartExcluded;

  @JsonProperty("kbartCoverageOwnership")
  private Boolean kbartCoverageOwnership;

  @JsonProperty("consortia")
  private String consortia;

  @JsonUnwrapped
  private UserDefinedFields userDefinedFields;
}
