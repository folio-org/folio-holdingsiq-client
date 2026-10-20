package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * Relationship between two titles, e.g. ABSORBED_BY, CONTINUED_BY, PARENT_OF.
 *
 * @param date ISO-8601 date-time of the relationship
 */
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record TitleRelationship(
  @JsonProperty("resourceRelationshipId") Integer resourceRelationshipId,
  @JsonProperty("source") RelatedTitle source,
  @JsonProperty("target") RelatedTitle target,
  @JsonProperty("type") String type,
  @JsonProperty("date") String date
) { }
