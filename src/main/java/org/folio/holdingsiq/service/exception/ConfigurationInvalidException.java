package org.folio.holdingsiq.service.exception;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.folio.holdingsiq.model.ConfigurationError;

@RequiredArgsConstructor
public class ConfigurationInvalidException extends RuntimeException {

  private static final long serialVersionUID = 5325789760372474463L;

  @Getter
  private final List<ConfigurationError> errors;
}
