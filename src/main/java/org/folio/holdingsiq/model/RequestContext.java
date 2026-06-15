package org.folio.holdingsiq.model;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.folio.okapi.common.XOkapiHeaders;

@Getter
public class RequestContext {

  private final Map<String, String> headers;
  private final String token;
  private final String tenant;
  private final String host;
  private final int port;
  private final String url;
  private final String userId;

  public RequestContext(Map<String, String> headers) {
    this.headers = new CaseInsensitiveMap<>(headers);
    this.token = this.headers.get(XOkapiHeaders.TOKEN);
    this.tenant = this.headers.get(XOkapiHeaders.TENANT);
    this.userId = this.headers.get(XOkapiHeaders.USER_ID);
    this.url = this.headers.get(XOkapiHeaders.URL);

    try {
      var parsedUrl = new URI(this.url).toURL();
      this.host = parsedUrl.getHost();
      this.port = parsedUrl.getPort() != -1 ? parsedUrl.getPort() : parsedUrl.getDefaultPort();
    } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
      throw new IllegalArgumentException("Url header does not contain valid url", e);
    }
  }

}

