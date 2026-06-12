package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum PackageFilterSelected {

  ALL("all"),
  SELECTED("selected"),
  NOT_SELECTED("notselected"),
  ORDERED_THROUGH_EBSCO("orderedthroughebsco");

  private final String value;

  public static boolean contains(String value) {
    for (PackageFilterSelected c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static PackageFilterSelected fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (PackageFilterSelected c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
