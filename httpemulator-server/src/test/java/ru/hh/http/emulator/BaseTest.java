package ru.hh.http.emulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hh.http.emulator.client.entity.HttpEntry;

public class BaseTest {
  protected static final ObjectMapper objectMapper = new ObjectMapper();

  private static JettyServer jetty;

  protected static int jettyPort;

  protected static HttpClient client = new HttpClient();

  protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @BeforeClass
  public static void beforeClass() throws Exception {
    objectMapper.registerModule(new Hibernate4Module());

    jetty = new JettyServer();
    jettyPort = jetty.start();

    client.setFollowRedirects(false);
    client.start();
  }

  @AfterClass
  public static void afterClass() throws Exception {
    client.stop();
    jetty.stop();
  }

  @Before
  public void beforeTestMethod() throws Exception {
    Assert.assertEquals(HttpServletResponse.SC_OK, deleteAll().getStatus());
  }

  @After
  public void afterTestMethod() throws Exception { }

  protected Request newRequest() {
    return client.newRequest("localhost", jettyPort);
  }

  protected ContentResponse deleteAll() throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    return newRequest().path("/criteria/all").method(HttpMethod.DELETE).send();
  }

  protected ContentResponse deleteCriteria(final long id) throws JsonProcessingException, InterruptedException, TimeoutException, ExecutionException {
    return newRequest().path("/criteria/" + id).method(HttpMethod.DELETE).send();
  }

  protected ContentResponse putSimple(final HttpEntry rule, final Collection<HttpEntry> responseEntries) throws JsonProcessingException,
    InterruptedException, TimeoutException, ExecutionException {
    return newRequest().path("/criteria/simple")
    .method(HttpMethod.PUT)
    .param("rule", objectMapper.writeValueAsString(rule))
    .param("response", objectMapper.writeValueAsString(responseEntries))
    .send();
  }
}
