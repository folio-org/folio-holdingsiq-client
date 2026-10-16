package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor(onConstructor_ = @JsonIgnore)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TitlePost {

  @JsonProperty("titleName")
  private String titleName;

  @JsonProperty("pubType")
  private String pubType;

  @JsonProperty("publisherName")
  private String publisherName;

  @JsonProperty("isPeerReviewed")
  private boolean isPeerReviewed;

  @JsonProperty("edition")
  private String edition;

  @JsonProperty("description")
  private String description;

  @JsonProperty("identifiersList")
  private List<Identifier> identifiersList;

  @JsonProperty("contributorsList")
  private List<Contributor> contributorsList;

  @JsonProperty("url")
  private String titleUrl;

  @JsonProperty("customCoverageList")
  private List<CoverageDates> customCoverageList;

  @JsonProperty("coverageStatement")
  private String coverageStatement;

  @JsonProperty("customEmbargoPeriod")
  private EmbargoPeriod customEmbargoPeriod;

  @JsonUnwrapped
  private UserDefinedFields userDefinedFields;

  @JsonProperty("proxy")
  private Proxy proxy;
}
