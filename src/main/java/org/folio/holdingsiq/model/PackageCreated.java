package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PackageCreated(@JsonProperty("packageId") Integer packageId) { }
