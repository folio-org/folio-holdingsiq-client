package org.folio.holdingsiq.service.exception;

import lombok.Getter;

public class ServiceResponseException extends ServiceException {

  private static final long serialVersionUID = 1L;

  @Getter
  private final Integer code;
  @Getter
  private final String query;
  @Getter
  private final String responseMessage;
  @Getter
  private final String responseBody;

  public ServiceResponseException(String message, Integer code, String rmapiMessage, String responseBody,
                                  String query) {
    super(message);
    this.code = code;
    this.responseMessage = rmapiMessage;
    this.responseBody = responseBody;
    this.query = query;
  }
}
