package ru.hh.httpemulator.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import ru.hh.httpemulator.client.entity.HttpCriteria;
import ru.hh.httpemulator.client.entity.HttpEntry;

public class EmulatorClient {

  private static final String PUT_SIMPLE_PATH = "/criteria/simple";

  private static final String CRITERIA_PATH = "/criteria";

  private static final String PUT_FULL_RULE_PATH = "/criteria/full";

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final HttpClient client = new HttpClient();

  private final String url;

  public EmulatorClient(String url) throws Exception {
    this.url = url;
    client.start();
  }

  public SimpleCriteriaBuilder createSimpleRule() {
    return new SimpleCriteriaBuilder(this);
  }

  public FullCriteriaBuilder createFullRule() {
    return new FullCriteriaBuilder(this);
  }

  protected Request newRequest() {
    return client.newRequest(url);
  }

  protected ContentResponse putSimple(HttpEntry rule, Collection<HttpEntry> responseEntries)
      throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    return newRequest().path(PUT_SIMPLE_PATH)
        .method(HttpMethod.PUT)
        .param("rule", MAPPER.writeValueAsString(rule))
        .param("response", MAPPER.writeValueAsString(responseEntries))
        .send();
  }

  public void deleteRule(long id) throws InterruptedException, TimeoutException, ExecutionException {
    final ContentResponse response = newRequest().path(CRITERIA_PATH)
        .method(HttpMethod.DELETE)
        .param("id", Long.toString(id))
        .send();

    if (response.getStatus() != 200) {
      throw new IllegalStateException("HTTP status code = " + response.getStatus());
    }
  }

  protected ContentResponse putRule(HttpCriteria criteria, Collection<HttpEntry> responseEntries)
      throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    return newRequest().path(PUT_FULL_RULE_PATH)
        .method(HttpMethod.PUT)
        .param("criteria", MAPPER.writeValueAsString(criteria))
        .param("response", MAPPER.writeValueAsString(responseEntries))
        .send();
  }

}
