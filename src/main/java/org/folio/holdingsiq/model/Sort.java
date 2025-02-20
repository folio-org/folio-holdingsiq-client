package org.folio.holdingsiq.model;

import lombok.Getter;

@Getter
public enum Sort {

  RELEVANCE("relevance"),
  NAME("name");

  private final String value;

  Sort(String value) {
    this.value = value;
  }

  public static boolean contains(String value) {
    for (Sort c : Sort.values()) {
      if (c.name().equals(value)) {
        return true;
      }
    }

    return false;
  }
}
