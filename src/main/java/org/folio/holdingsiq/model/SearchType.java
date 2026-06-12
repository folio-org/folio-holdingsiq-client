package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum SearchType {

  EXACT_PHRASE("exactphrase"),
  ADVANCED("advanced"),
  ANY("any"),
  CONTAINS("contains"),
  EXACT_MATCH("exactmatch"),
  BEGINS_WITH("beginswith");

  private final String value;

  public static boolean contains(String value) {
    for (SearchType c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static SearchType fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (SearchType c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
