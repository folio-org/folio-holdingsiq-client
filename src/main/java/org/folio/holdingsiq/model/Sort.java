package org.folio.holdingsiq.model;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum Sort {

  RELEVANCE("relevance"),
  NAME("name");

  private final String value;

  public static boolean contains(String value) {
    for (Sort c : Sort.values()) {
      if (c.name().equals(value)) {
        return true;
      }
    }

    return false;
  }

  public static Sort fromValue(String value) {
    for (Sort c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
