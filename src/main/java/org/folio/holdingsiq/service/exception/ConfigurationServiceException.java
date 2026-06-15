package org.folio.holdingsiq.service.exception;

import lombok.Getter;

public class ConfigurationServiceException extends RuntimeException {
  private static final long serialVersionUID = 367568234830759007L;
  @Getter
  private final String responseBody;
  @Getter
  private final Integer statusCode;

  public ConfigurationServiceException(String responseBody, Integer statusCode) {
    super("Status code: " + statusCode);
    this.responseBody = responseBody;
    this.statusCode = statusCode;
  }
}
