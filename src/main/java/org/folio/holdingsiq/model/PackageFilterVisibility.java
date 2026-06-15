package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum PackageFilterVisibility {

  VISIBLE_SOMEWHERE("visibleSomewhere"),
  VISIBLE_IN_PF("visibleInPF"),
  VISIBLE_IN_FTF("visibleInFTF"),
  INCLUDED_IN_MARC("includedInMARC"),
  HIDDEN_SOMEWHERE("hiddenSomewhere"),
  HIDDEN_IN_PF("hiddenInPF"),
  HIDDEN_IN_FTF("hiddenInFTF"),
  EXCLUDED_FROM_MARC("excludedFromMARC");

  private final String value;

  public static boolean contains(String value) {
    for (PackageFilterVisibility c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static PackageFilterVisibility fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (PackageFilterVisibility c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
