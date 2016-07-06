package ru.hh.httpemulator.server.scenario;

import java.util.Collection;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.CollectionUtils;
import ru.hh.httpemulator.client.entity.AttributeType;
import ru.hh.httpemulator.client.entity.HttpEntry;

@Named(value = OAuthCodeCallbackScenario.SCENARIO_NAME)
@Singleton
public class OAuthCodeCallbackScenario implements Scenario {
  public static final String SCENARIO_NAME = "oAuthCodeCallbackScenario";
  public static final String OAUTH_REDIRECT_URI_KEY = "oauth_redirect_uri";

  @Override
  public Collection<HttpEntry> execute(HttpServletRequest request, HttpServletResponse response, Collection<HttpEntry> otherEntries) {
    final String state = request.getParameter("state");
    if (state == null) {
      return null;
    }

    if (!CollectionUtils.isEmpty(otherEntries)) {
      otherEntries.stream()
          .filter(httpEntry -> httpEntry.getType().equals(AttributeType.PARAMETER))
          .filter(httpEntry -> OAUTH_REDIRECT_URI_KEY.equals(httpEntry.getKey()))
          .map(HttpEntry::getValue)
          .findFirst()
          .ifPresent(oauthRedirectUri
              -> response.setHeader("Location", oauthRedirectUri + (oauthRedirectUri.indexOf('?') == -1 ? '?' : '&') + "state=" + state));
    }

    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);

    return null;
  }

}
