package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubjectFacet(
  @JsonProperty("id") Long id,
  @JsonProperty("parentId") Long parentId,
  @JsonProperty("schema") String subjectSchema,
  @JsonProperty("name") String subjectName,
  @JsonProperty("count") Integer totalCount
) {
}
