package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import org.folio.holdingsiq.deserializer.AlternateNameListDeserializer;

@Value
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackagePut {

  @JsonProperty("packageName")
  String packageName;

  @JsonProperty(value = "contentType")
  Integer contentType;

  @JsonProperty("isSelected")
  Boolean isSelected;

  @JsonProperty("visibility")
  List<Visibility> visibilityDetails;

  @JsonProperty("customDescription")
  String customDescription;

  @JsonProperty("url")
  String packageUrl;

  @JsonProperty("isFullPackage")
  Boolean isFullPackage;

  @JsonProperty("allowEbscoToAddTitles")
  Boolean allowEbscoToAddTitles;

  @JsonProperty("customCoverage")
  CoverageDates customCoverage;

  @JsonProperty("proxy")
  Proxy proxy;

  @JsonProperty("packageToken")
  TokenInfo packageToken;

  @JsonProperty("customAltNames")
  @JsonDeserialize(using = AlternateNameListDeserializer.class)
  List<AlternateName> customAltNames;

  @JsonProperty("customDisplayName")
  String customDisplayName;

  @JsonProperty("packageFreeAccess")
  Boolean packageFreeAccess;
}
