package org.folio.holdingsiq.service.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.folio.holdingsiq.service.exception.RequestValidationException;
import org.junit.jupiter.api.Test;

class PackageParametersValidatorTest {

  private final PackageParametersValidator validator = new PackageParametersValidator();

  @Test
  void shouldNotThrowExceptionWhenParametersAreValid() {
    validator.validate(null, null, "relevance", "query", "name");
  }

  @Test
  void shouldThrowExceptionWhenFilterSelectedIsInvalid() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("notall", null, "relevance", "query", "name"));
  }

  @Test
  void shouldThrowExceptionWhenFilterTypeIsInvalid() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("selected", "notall", "relevance", "query", "name"));
  }

  @Test
  void shouldThrowExceptionWhenSortIsInvalid() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("selected", "all", "abc", "query", "name"));
  }

  @Test
  void shouldThrowExceptionWhenSearchQueryIsEmpty() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("selected", "all", "abc", "", "name"));
  }

  @Test
  void shouldThrowExceptionWhenSearchFieldIsInvalid() {
    assertThrows(RequestValidationException.class, () ->
      validator.validate("selected", "all", "abc", "query", "invalid"));
  }

}
