package org.folio.holdingsiq.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import lombok.Getter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.folio.okapi.common.XOkapiHeaders;

@Getter
public class OkapiData {

  private final Map<String, String> headers;
  private final String apiToken;
  private final String tenant;
  private final String okapiHost;
  private final int okapiPort;
  private final String okapiUrl;
  private final String userId;

  public OkapiData(Map<String, String> headers) {
    this.headers = new CaseInsensitiveMap<>(headers);

    try {
      apiToken = this.headers.get(XOkapiHeaders.TOKEN);
      tenant = this.headers.get(XOkapiHeaders.TENANT);
      userId = this.headers.get(XOkapiHeaders.USER_ID);
      okapiUrl = this.headers.get(XOkapiHeaders.URL);
      URL url = new URL(okapiUrl);
      okapiHost = url.getHost();
      okapiPort = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Okapi url header does not contain valid url", e);
    }
  }

}

