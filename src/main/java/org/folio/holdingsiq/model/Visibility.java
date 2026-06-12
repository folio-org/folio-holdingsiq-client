package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Visibility(
  @JsonProperty("category") String category,
  @JsonProperty("hidden") Boolean hidden,
  @JsonProperty("reason") String reason
) { }
