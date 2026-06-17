package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubjectAssociation(
  @JsonProperty("id") Integer id,
  @JsonProperty("schemaId") Integer schemaId,
  @JsonProperty("name") String name,
  @JsonProperty("isCustom") Boolean isCustom,
  @JsonProperty("explicitAssignment") Boolean explicitAssignment,
  @JsonProperty("subjectHierarchy") String subjectHierarchy
) { }
