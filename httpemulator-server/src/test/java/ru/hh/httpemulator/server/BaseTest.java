package ru.hh.httpemulator.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import ru.hh.httpemulator.client.entity.HttpEntry;

public class BaseTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static JettyServer jetty;

  private static int jettyPort;

  private static final HttpClient HTTP_CLIENT = new HttpClient();

  @BeforeClass
  public static void beforeClass() throws Exception {
    MAPPER.registerModule(new Hibernate5Module());

    jetty = new JettyServer();
    jettyPort = jetty.start();

    HTTP_CLIENT.setFollowRedirects(false);
    HTTP_CLIENT.start();
  }

  @AfterClass
  public static void afterClass() throws Exception {
    HTTP_CLIENT.stop();
    jetty.stop();
  }

  @Before
  public void beforeTestMethod() throws InterruptedException, TimeoutException, ExecutionException {
    Assert.assertEquals(HttpServletResponse.SC_OK, deleteAll().getStatus());
  }

  @After
  public void afterTestMethod() {
  }

  protected Request newRequest() {
    return HTTP_CLIENT.newRequest("localhost", jettyPort);
  }

  protected ContentResponse deleteAll() throws InterruptedException, TimeoutException, ExecutionException {
    return newRequest().path("/criteria/all").method(HttpMethod.DELETE).send();
  }

  protected ContentResponse deleteCriteria(long id) throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    return newRequest().path("/criteria/" + id).method(HttpMethod.DELETE).send();
  }

  protected ContentResponse putSimple(HttpEntry rule, Collection<HttpEntry> responseEntries)
      throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    return newRequest().path("/criteria/simple")
    .method(HttpMethod.PUT)
        .param("rule", MAPPER.writeValueAsString(rule))
        .param("response", MAPPER.writeValueAsString(responseEntries))
        .send();
  }
}
