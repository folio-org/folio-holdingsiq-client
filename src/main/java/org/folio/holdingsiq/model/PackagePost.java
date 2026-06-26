package org.folio.holdingsiq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import org.folio.holdingsiq.deserializer.AlternateNameListDeserializer;

@Value
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackagePost {

  @JsonProperty("contentType")
  int contentType;

  @JsonProperty("customAltNames")
  @JsonDeserialize(using = AlternateNameListDeserializer.class)
  List<AlternateName> customAltNames;

  @JsonProperty("customDescription")
  String customDescription;

  @JsonProperty("customDisplayName")
  String customDisplayName;

  @JsonProperty("customCoverage")
  CoverageDates coverage;

  @JsonProperty("packageFreeAccess")
  Boolean packageFreeAccess;

  @JsonProperty("packageName")
  String packageName;

  @JsonProperty("proxy")
  Proxy proxy;

  @JsonProperty("subjects")
  List<Integer> subjectIds;

  @JsonProperty("url")
  String packageUrl;
}
