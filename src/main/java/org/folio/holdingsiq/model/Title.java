package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
public class Title {

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

  @JsonProperty("customerResourcesList")
  private List<CustomerResources> customerResourcesList;

  @JsonProperty("description")
  private String description;

  @JsonProperty("edition")
  private String edition;

  @JsonProperty("isPeerReviewed")
  private Boolean isPeerReviewed;

  @JsonProperty("contributorsList")
  private List<Contributor> contributorsList;

  /**
   * Populated only when the title is requested with {@code includerelationships=true}.
   */
  @JsonProperty("titleRelationships")
  private List<TitleRelationship> titleRelationships;
}
