package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum TitleSearchField {

  TITLE_NAME("titlename"),
  PUBLISHER("publisher"),
  ISXN("isxn"),
  SUBJECT("subject"),
  ZDBID("zdbid");

  private final String value;

  public static boolean contains(String value) {
    for (TitleSearchField c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static TitleSearchField fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (TitleSearchField c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
