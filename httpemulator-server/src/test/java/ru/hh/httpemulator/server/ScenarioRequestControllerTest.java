package ru.hh.httpemulator.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StopWatch;

import ru.hh.httpemulator.client.entity.AttributeType;
import ru.hh.httpemulator.client.entity.HttpEntry;
import ru.hh.httpemulator.server.exception.AmbiguousRulesException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import ru.hh.httpemulator.server.scenario.OAuthCodeCallbackScenario;

public class ScenarioRequestControllerTest extends BaseTest {

  @Test
  public void executeTimeoutScenarioTest() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException,
      AmbiguousRulesException, InterruptedException, TimeoutException, ExecutionException {

    final int time = 2000;

    final ContentResponse criteriaResponse = putSimple(new HttpEntry(AttributeType.PARAMETER, "param1", "value1"),
        Arrays.asList(new HttpEntry(AttributeType.STATUS, null, "548"),
            new HttpEntry(AttributeType.SCENARIO, null, "timeoutScenario"),
            new HttpEntry(AttributeType.PARAMETER, "timeout", Integer.toString(time))));

    Assert.assertEquals(HttpServletResponse.SC_OK, criteriaResponse.getStatus());

    final StopWatch watch = new StopWatch();
    watch.start();

    final ContentResponse response = newRequest()
        .path("/abc/def")
        .method(HttpMethod.GET)
        .param("param1", "value1")
        .send();

    watch.stop();

    Assert.assertEquals(548, response.getStatus());
    Assert.assertTrue(watch.getTotalTimeMillis() > time);
  }

  @Test
  public void executeOAuthStateScenarioTest() throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {

    final String state = "oauthState";
    final String redirectUri = "https://localhost/code";

    final ContentResponse criteriaResponse;
    criteriaResponse = putSimple(new HttpEntry(AttributeType.PARAMETER, "test", "oAuth"),
        Arrays.asList(new HttpEntry(AttributeType.PARAMETER, OAuthCodeCallbackScenario.OAUTH_REDIRECT_URI_KEY, redirectUri),
            new HttpEntry(AttributeType.SCENARIO, null, OAuthCodeCallbackScenario.SCENARIO_NAME)));

    Assert.assertEquals(HttpServletResponse.SC_OK, criteriaResponse.getStatus());

    final ContentResponse response = newRequest()
        .path("/abc/def")
        .method(HttpMethod.GET)
        .param("test", "oAuth")
        .param("state", state)
        .send();

    Assert.assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, response.getStatus());
    Assert.assertEquals(redirectUri + "?state=" + state, response.getHeaders().get("Location"));
  }

}
