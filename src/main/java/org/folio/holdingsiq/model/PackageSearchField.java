package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum PackageSearchField {

  NAME("name"),
  KEYWORD("keyword");

  private final String value;

  public static boolean contains(String value) {
    for (PackageSearchField c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static PackageSearchField fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (PackageSearchField c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
