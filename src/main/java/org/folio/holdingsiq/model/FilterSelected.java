package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum FilterSelected {

  ALL("all"),
  SELECTED("selected"),
  NOT_SELECTED("notselected"),
  ORDERED_THROUGH_EBSCO("orderedthroughebsco");

  private final String value;

  public static boolean contains(String value) {
    for (FilterSelected c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static FilterSelected fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (FilterSelected c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
