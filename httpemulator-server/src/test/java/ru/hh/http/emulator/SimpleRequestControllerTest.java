package ru.hh.http.emulator;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Assert;
import org.junit.Test;

import ru.hh.http.emulator.client.entity.AttributeType;
import ru.hh.http.emulator.client.entity.HttpEntry;
import ru.hh.http.emulator.exception.AmbiguousRulesException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


public class SimpleRequestControllerTest extends BaseTest{
	
	@Test
    public void putSimpleRuleTest() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException, AmbiguousRulesException, InterruptedException, TimeoutException, ExecutionException{
		
		final ContentResponse criteriaResponse = putSimple(new HttpEntry(AttributeType.PARAMETER, "param1", "value1"), Arrays.asList(new HttpEntry(AttributeType.STATUS, null, "123")));
		Assert.assertEquals(HttpServletResponse.SC_OK, criteriaResponse.getStatus());
		
		final long id = Long.parseLong(new String(criteriaResponse.getContent()));
		Assert.assertTrue(id >= 0);
		
		final ContentResponse response = newRequest()
				.path("/abc/def")
				.method(HttpMethod.GET)
				.param("param1", "value1")
				.send();
		
		Assert.assertEquals(123, response.getStatus());
    }
	
	@Test
    public void requestWithNotExistentRuleTest() throws InterruptedException, TimeoutException, ExecutionException{
		
		final ContentResponse response = newRequest()
				.path("/abc/def")
				.method(HttpMethod.GET)
				.param("param1", "value1")
				.send();
		
		Assert.assertEquals(404, response.getStatus());
    }
	
	@Test
    public void requestWithAmbiguousRuleTest() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException, AmbiguousRulesException, InterruptedException, TimeoutException, ExecutionException{
		
		ContentResponse criteriaResponse = putSimple(new HttpEntry(AttributeType.PARAMETER, "param1", "value1"), Arrays.asList(new HttpEntry(AttributeType.STATUS, null, "123")));
		Assert.assertEquals(HttpServletResponse.SC_OK, criteriaResponse.getStatus());
		criteriaResponse = putSimple(new HttpEntry(AttributeType.PARAMETER, "param2", "value2"), Arrays.asList(new HttpEntry(AttributeType.STATUS, null, "321")));
		Assert.assertEquals(HttpServletResponse.SC_OK, criteriaResponse.getStatus());
		
		final ContentResponse response = newRequest()
				.path("/abc/def")
				.method(HttpMethod.GET)
				.param("param1", "value1")
				.param("param2", "value2")
				.send();
		
		Assert.assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
    }
	
	@Test
    public void deleteRuleTest() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException, AmbiguousRulesException, InterruptedException, TimeoutException, ExecutionException{
		
		ContentResponse criteriaResponse = putSimple(new HttpEntry(AttributeType.PARAMETER, "param1", "value1"), Arrays.asList(new HttpEntry(AttributeType.STATUS, null, "123")));
		Assert.assertEquals(HttpServletResponse.SC_OK, criteriaResponse.getStatus());
		
		final long id = Long.parseLong(new String(criteriaResponse.getContent()));
		criteriaResponse = deleteCriteria(id);
		Assert.assertEquals(HttpServletResponse.SC_OK, criteriaResponse.getStatus());
		
		final ContentResponse response = newRequest()
				.path("/abc/def")
				.method(HttpMethod.GET)
				.param("param1", "value1")
				.send();
		
		Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }
}
