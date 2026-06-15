package org.folio.holdingsiq.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ConfigurationError implements Serializable {

  private static final long serialVersionUID = -6757895174756465024L;

  @Getter
  private String message;
}
