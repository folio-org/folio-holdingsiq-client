package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Packages {

  @JsonProperty("packagesList")
  List<PackageData> packagesList;

  @JsonProperty("facets")
  Facets facets;

  @JsonProperty("totalResults")
  Integer totalResults;
}
