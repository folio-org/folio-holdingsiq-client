package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum PackageFilterType {

  ALL("all"),
  AGGREGATED_FULL_TEXT("aggregatedfulltext"),
  ABSTRACT_AND_INDEX("abstractandindex"),
  EBOOK("ebook"),
  EJOURNAL("ejournal"),
  PRINT("print"),
  UNKNOWN("unknown"),
  ONLINE_REFERENCE("onlinereference"),
  STREAMING_MEDIA("streamingmedia"),
  MIXED_CONTENT("mixedcontent");

  private final String value;

  public static boolean contains(String value) {
    for (PackageFilterType c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static PackageFilterType fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (PackageFilterType c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
