package org.folio.holdingsiq.service.validator;

import org.folio.holdingsiq.service.exception.RequestValidationException;
import org.junit.Test;

public class PackageParametersValidatorTest {

  private final PackageParametersValidator validator = new PackageParametersValidator();

  @Test
  public void shouldNotThrowExceptionWhenParametersAreValid() {
    validator.validate(null, null, "relevance", "query");
  }

  @Test(expected = RequestValidationException.class)
  public void shouldThrowExceptionWhenFilterSelectedIsInvalid() {
    validator.validate("notall", null, "relevance", "query");
  }

  @Test(expected = RequestValidationException.class)
  public void shouldThrowExceptionWhenFilterTypeIsInvalid() {
    validator.validate("selected", "notall", "relevance", "query");
  }

  @Test(expected = RequestValidationException.class)
  public void shouldThrowExceptionWhenSortIsInvalid() {
    validator.validate("selected", "all", "abc", "query");
  }

  @Test(expected = RequestValidationException.class)
  public void shouldThrowExceptionWhenSearchQueryIsEmpty() {
    validator.validate("selected", "all", "abc", "");
  }

}
