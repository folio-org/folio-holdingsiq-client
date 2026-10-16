package org.folio.holdingsiq.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

/**
 * Search results of the v2 {@code /lists/{listid}/titles} endpoint.
 */
@Value
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resources {

  @JsonProperty("titles")
  List<Resource> titles;

  @JsonProperty("facets")
  Facets facets;

  @JsonProperty("totalResults")
  Integer totalResults;
}
