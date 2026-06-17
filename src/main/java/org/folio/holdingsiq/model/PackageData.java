package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.folio.holdingsiq.deserializer.AlternateNameListDeserializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageData {

  @JsonProperty("listId")
  private Integer packageId;

  @JsonProperty("packageName")
  private String packageName;

  @JsonProperty("customDisplayName")
  private String customDisplayName;

  @JsonProperty("managedAltNames")
  @JsonDeserialize(using = AlternateNameListDeserializer.class)
  private List<AlternateName> managedAltNames;

  @JsonProperty("customAltNames")
  @JsonDeserialize(using = AlternateNameListDeserializer.class)
  private List<AlternateName> customAltNames;

  @JsonProperty("managedDescription")
  private String managedDescription;

  @JsonProperty("customDescription")
  private String customDescription;

  @JsonProperty("isCustom")
  private Boolean isCustom;

  @JsonProperty("vendorId")
  private Integer vendorId;

  @JsonProperty("vendorName")
  private String vendorName;

  @JsonProperty("titleCount")
  private Integer titleCount;

  @JsonProperty("isSelected")
  private Boolean isSelected;

  @JsonProperty("isOrderedThroughEbsco")
  private Boolean isOrderedThroughEbsco;

  @JsonProperty("packageFreeAccess")
  private Boolean packageFreeAccess;

  @JsonProperty("visibility")
  private List<Visibility> visibilityDetails;

  @JsonProperty("selectedCount")
  private Integer selectedCount;

  @JsonProperty("isTokenNeeded")
  private Boolean isTokenNeeded;

  @JsonProperty("customCoverage")
  private CoverageDates customCoverage;

  @JsonProperty("contentType")
  private String contentType;

  @JsonProperty("packageType")
  private String packageType;

  @JsonProperty("availableForSelection")
  private Boolean availableForSelection;

  @JsonProperty("url")
  private String packageUrl;

  @JsonProperty("proxiedUrl")
  private String proxiedUrl;

  @JsonProperty("subjectAssociations")
  private List<SubjectAssociation> subjectAssociations;

  @JsonProperty("consortia")
  private String consortia;

  @JsonProperty("isPrimaryPackage")
  private Boolean isPrimaryPackage;

  @JsonProperty("allowEbscoToAddTitles")
  private Boolean allowEbscoToAddTitles;

  @JsonProperty("proxy")
  private Proxy proxy;

  @JsonProperty("packageToken")
  private TokenInfo packageToken;

  @JsonProperty("isSmartLinksPlusEligible")
  private Boolean isSmartLinksPlusEligible;

  public String getFullPackageId() {
    return getVendorId() + "-" + getPackageId();
  }
}
