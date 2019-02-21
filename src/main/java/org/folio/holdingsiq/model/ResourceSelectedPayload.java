package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceSelectedPayload {
  @JsonProperty("isSelected")
  private boolean isSelected;

  @JsonProperty("titleName")
  private String titleName;

  @JsonProperty("pubType")
  private String pubType;

  @JsonProperty("url")
  private String url;
}
