package org.folio.holdingsiq.service.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.folio.holdingsiq.model.FilterQuery;
import org.folio.holdingsiq.service.exception.RequestValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitleParametersValidatorTest {

  private final TitleParametersValidator validator = new TitleParametersValidator();
  private FilterQuery.FilterQueryBuilder fqb;

  @BeforeEach
  void setUp() {
    fqb = FilterQuery.builder();
  }

  @Test
  void shouldNotThrowExceptionWhenParametersAreValid() {
    validator.validate(fqb.selected("selected").type("book").name("ebsco").build(), "relevance");
  }

  @Test
  void shouldNotThrowExceptionWhenNoFilterParametersAndAllowNullTrue() {
    validator.validate(fqb
      .selected(null).type(null)
      .name(null).isxn(null).subject(null)
      .publisher(null).build(), "relevance", true);
  }

  @Test
  void shouldThrowExceptionWhenSelectedParameterIsInvalid() {
    var filterQuery = fqb.selected("doNotEnter").type("book").name("ebsco").build();
    assertThrows(RequestValidationException.class, () -> validator.validate(filterQuery, "relevance"));
  }

  @Test
  void shouldThrowExceptionWhenTypeParameterIsInvalid() {
    var filterQuery = fqb.selected("selected").type("doNotEnter").name("ebsco").build();
    assertThrows(RequestValidationException.class, () -> validator.validate(filterQuery, "relevance"));
  }

  @Test
  void shouldThrowExceptionWhenThereAreConflictingParameters() {
    var filterQuery = fqb.selected("selected").type("book").name("ebsco").subject("history").build();
    assertThrows(RequestValidationException.class, () -> validator.validate(filterQuery, "relevance"));
  }

  /* One of filter[name], filter[isxn], filter[subject] or filter[publisher] is required */
  @Test
  void shouldThrowExceptionWhenAtLeastOneRequiredFilterParametersIsNotProvided() {
    var filterQuery = fqb.selected("selected").type("book").build();
    assertThrows(RequestValidationException.class, () -> validator.validate(filterQuery, null));
  }

  @Test
  void shouldThrowExceptionWhenFilterNameParameterIsEmpty() {
    var filterQuery = fqb.selected("selected").type("book").name("").build();
    assertThrows(RequestValidationException.class, () -> validator.validate(filterQuery, "relevance"));
  }

  @Test
  void shouldThrowExceptionWhenFilterIsxnParameterIsEmpty() {
    var filterQuery = fqb.selected("selected").type("book").isxn("").build();
    assertThrows(RequestValidationException.class, () -> validator.validate(filterQuery, null));
  }

  @Test
  void shouldThrowExceptionWhenFilterSubjectParameterIsEmpty() {
    var filterQuery = fqb.selected("selected").type("book").subject("").build();
    assertThrows(RequestValidationException.class, () -> validator.validate(filterQuery, null));
  }

  @Test
  void shouldThrowExceptionWhenFilterPublisherParameterIsEmpty() {
    var filterQuery = fqb.selected("selected").type("book").publisher("").build();
    assertThrows(RequestValidationException.class, () -> validator.validate(filterQuery, null));
  }
}
