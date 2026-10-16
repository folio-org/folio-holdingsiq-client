package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum FilterFreeAccess {

  ALL("all"),
  TRUE("true"),
  FALSE("false");

  private final String value;

  public static boolean contains(String value) {
    for (FilterFreeAccess c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static FilterFreeAccess fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (FilterFreeAccess c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
