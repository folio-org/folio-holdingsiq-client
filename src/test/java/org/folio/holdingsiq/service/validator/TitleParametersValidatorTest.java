package org.folio.holdingsiq.service.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.folio.holdingsiq.service.exception.RequestValidationException;
import org.junit.jupiter.api.Test;

class TitleParametersValidatorTest {

  private final TitleParametersValidator validator = new TitleParametersValidator();

  @Test
  void shouldNotThrowExceptionWhenParametersAreValid() {
    validator.validate("selected", "book", "relevance", "query", "titlename");
  }

  @Test
  void shouldNotThrowExceptionWhenOptionalParametersAreMissing() {
    validator.validate(null, null, "relevance", null, null);
  }

  @Test
  void shouldNotThrowExceptionWhenSearchFieldIsZdbid() {
    validator.validate(null, null, "name", "2071469-8", "zdbid");
  }

  @Test
  void shouldThrowExceptionWhenFilterSelectedIsInvalid() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("notall", null, "relevance", "query", "titlename"));
  }

  @Test
  void shouldThrowExceptionWhenFilterTypeIsInvalid() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("selected", "notall", "relevance", "query", "titlename"));
  }

  @Test
  void shouldThrowExceptionWhenSortIsInvalid() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("selected", "all", "abc", "query", "titlename"));
  }

  @Test
  void shouldThrowExceptionWhenSearchQueryIsEmpty() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("selected", "all", "relevance", "", "titlename"));
  }

  @Test
  void shouldThrowExceptionWhenSearchFieldIsInvalid() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("selected", "all", "relevance", "query", "invalid"));
  }
}
