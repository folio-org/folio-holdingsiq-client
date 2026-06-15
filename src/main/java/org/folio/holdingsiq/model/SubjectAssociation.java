package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubjectAssociation(
  @JsonProperty("id") Long id,
  @JsonProperty("schemaId") Long schemaId,
  @JsonProperty("name") String name,
  @JsonProperty("isCustom") Boolean isCustom,
  @JsonProperty("explicitAssignment") Boolean explicitAssignment,
  @JsonProperty("subjectHierarchy") String subjectHierarchy
) { }
