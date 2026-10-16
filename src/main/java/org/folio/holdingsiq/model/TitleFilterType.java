package org.folio.holdingsiq.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum TitleFilterType {

  ALL("all"),
  JOURNAL("journal"),
  NEWSLETTER("newsletter"),
  REPORT("report"),
  PROCEEDINGS("proceedings"),
  WEBSITE("website"),
  NEWSPAPER("newspaper"),
  UNSPECIFIED("unspecified"),
  BOOK("book"),
  BOOK_SERIES("bookseries"),
  DATABASE("database"),
  THESIS_DISSERTATION("thesisdissertation"),
  STREAMING_AUDIO("streamingaudio"),
  STREAMING_VIDEO("streamingvideo"),
  AUDIOBOOK("audiobook");

  private final String value;

  public static boolean contains(String value) {
    for (TitleFilterType c : values()) {
      if (c.value.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public static TitleFilterType fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (TitleFilterType c : values()) {
      if (c.value.equalsIgnoreCase(value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
